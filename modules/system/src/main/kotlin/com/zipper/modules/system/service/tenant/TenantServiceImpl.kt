package com.zipper.modules.system.service.tenant

import cn.dev33.satoken.secure.BCrypt
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.zipper.framework.core.constant.Constants
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.domain.event.TenantCreateEvent
import com.zipper.framework.core.domain.event.TenantSyncPackageEvent
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.modules.system.domain.entity.*
import com.zipper.modules.system.mapper.*
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class TenantServiceImpl(
    private val userMapper: SysUserMapper,
    private val deptMapper: SysDeptMapper,
    private val roleMapper: SysRoleMapper,
    private val roleMenuMapper: SysRoleMenuMapper,
    private val roleDeptMapper: SysRoleDeptMapper,
    private val userRoleMapper: SysUserRoleMapper,
    private val dictTypeMapper: SysDictTypeMapper,
    private val dictDataMapper: SysDictDataMapper,
    private val configMapper: SysConfigMapper
) {


    /**
     * 租户创建事件
     */
    @EventListener
    fun handleCreateEvent(event: TenantCreateEvent) {
        // 根据套餐创建角色
        val roleId = createTenantRole(event.tenantId, event.tenantPackageMenuIds)
        // 创建部门: 公司名是部门名称
        val dept = SysDeptEntity()
        dept.tenantId = event.tenantId
        dept.deptName = event.companyName
        dept.parentId = Constants.TOP_PARENT_ID
        dept.ancestors = Constants.TOP_PARENT_ID.toString()
        deptMapper.insert(dept)
        val deptId = dept.deptId

        // 角色和部门关联表
        val roleDept = SysRoleDeptEntity()
        roleDept.roleId = roleId
        roleDept.deptId = deptId
        roleDeptMapper.insert(roleDept)

        // 创建系统用户
        val user = SysUserEntity()
        user.tenantId = event.tenantId
        user.userName = event.username
        user.nickName = event.username
        user.password = BCrypt.hashpw(event.password)
        user.deptId = deptId
        userMapper.insert(user)
        //新增系统用户后，默认当前用户为部门的负责人
        val sd = SysDeptEntity()
        sd.leader = user.userId
        sd.deptId = deptId
        deptMapper.updateById(sd)

        // 用户和角色关联表
        val userRole = SysUserRoleEntity()
        userRole.userId = user.userId
        userRole.roleId = roleId
        userRoleMapper.insert(userRole)

        val defaultTenantId = TenantConstants.DEFAULT_TENANT_ID
        val dictTypeList = dictTypeMapper.selectList(
            KtQueryWrapper(SysDictTypeEntity::class.java).eq(SysDictTypeEntity::tenantId, defaultTenantId)
        )
        val dictDataList = dictDataMapper.selectList(
            KtQueryWrapper(SysDictDataEntity::class.java).eq(SysDictDataEntity::tenantId, defaultTenantId)
        )
        for (dictType in dictTypeList) {
            dictType.dictId = null
            dictType.tenantId = event.tenantId
        }
        for (dictData in dictDataList) {
            dictData.dictCode = null
            dictData.tenantId = event.tenantId
        }
        dictTypeMapper.insertBatch(dictTypeList)
        dictDataMapper.insertBatch(dictDataList)

        val sysConfigList = configMapper.selectList(
            KtQueryWrapper(SysConfigEntity::class.java).eq(SysConfigEntity::tenantId, defaultTenantId)
        )
        for (config in sysConfigList) {
            config.configId = null
            config.tenantId = event.tenantId
        }
        configMapper.insertBatch(sysConfigList)
    }

    /**
     * 根据租户菜单创建租户角色
     *
     * @param tenantId  租户编号
     * @param packageMenuIds 租户菜单id列表
     * @return 角色id
     */
    private fun createTenantRole(tenantId: String, packageMenuIds: List<Long>): Long {
        // 创建角色
        val role = SysRoleEntity()
        role.tenantId = tenantId
        role.roleName = TenantConstants.TENANT_ADMIN_ROLE_NAME
        role.roleKey = TenantConstants.TENANT_ADMIN_ROLE_KEY
        role.roleSort = 1
        role.status = TenantConstants.NORMAL
        roleMapper.insert(role)
        val roleId = role.roleId

        // 创建角色菜单
        val roleMenus = packageMenuIds.map {
            val roleMenu = SysRoleMenuEntity()
            roleMenu.roleId = roleId
            roleMenu.menuId = it
            roleMenu
        }
        roleMenuMapper.insertBatch(roleMenus)

        return roleId
    }

    @EventListener
    fun onSyncTenantPackage(event: TenantSyncPackageEvent) {
        val roles = roleMapper.selectList(
            KtQueryWrapper(SysRoleEntity::class.java).eq(SysRoleEntity::tenantId, event.tenantId)
        )
        val roleIds = ArrayList<Long>(roles.size - 1)

        roles.forEach { item ->
            if (TenantConstants.TENANT_ADMIN_ROLE_KEY == item.roleKey) {
                val roleMenus = event.menuIds.map { menuId ->
                    val roleMenu = SysRoleMenuEntity()
                    roleMenu.roleId = item.roleId
                    roleMenu.menuId = menuId
                    roleMenu
                }
                roleMenuMapper.delete(
                    MybatisKt.ktQuery<SysRoleMenuEntity>()
                        .eq(SysRoleMenuEntity::roleId, item.roleId)
                )
                roleMenuMapper.insertBatch(roleMenus)
            } else {
                roleIds.add(item.roleId)
            }
        }
        if (roleIds.isNotEmpty()) {
            roleMenuMapper.delete(
                MybatisKt.ktQuery<SysRoleMenuEntity>()
                    .`in`(SysRoleMenuEntity::roleId, roleIds)
                    .notIn(event.menuIds.isNotEmpty(), SysRoleMenuEntity::menuId, event.menuIds)
            )
        }
    }
}