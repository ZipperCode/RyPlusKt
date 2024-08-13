package com.zipper.modules.system.service.role

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ObjectUtil
import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.modules.system.domain.bo.SysRoleBo
import com.zipper.modules.system.domain.entity.SysRoleDeptEntity
import com.zipper.modules.system.domain.entity.SysRoleEntity
import com.zipper.modules.system.domain.entity.SysRoleMenuEntity
import com.zipper.modules.system.domain.entity.SysUserRoleEntity
import com.zipper.modules.system.domain.vo.SysRoleVo
import com.zipper.modules.system.mapper.SysRoleDeptMapper
import com.zipper.modules.system.mapper.SysRoleMapper
import com.zipper.modules.system.mapper.SysRoleMenuMapper
import com.zipper.modules.system.mapper.SysUserRoleMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.framework.core.utils.StringUtilsExt
import java.util.*

/**
 * 角色 业务层处理
 *
 * @author Lion Li
 */
@Service
class SysRoleServiceImpl(
    private val baseMapper: SysRoleMapper,
    private val roleMenuMapper: SysRoleMenuMapper,
    private val userRoleMapper: SysUserRoleMapper,
    private val roleDeptMapper: SysRoleDeptMapper
) : ISysRoleService {
    override fun selectPageRoleList(role: SysRoleBo, pageQuery: PageQuery): TableDataInfo<SysRoleVo> {
        val page = baseMapper.selectPageRoleList(pageQuery.build(), this.buildQueryWrapper(role))
        return TableDataInfo.build(page)
    }

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    override fun selectRoleList(role: SysRoleBo): List<SysRoleVo> {
        return baseMapper.selectRoleList(this.buildQueryWrapper(role))
    }

    private fun buildQueryWrapper(bo: SysRoleBo): Wrapper<SysRoleEntity> {
        val params = bo.params
        val wrapper = Wrappers.query<SysRoleEntity>()
        wrapper.eq("r.del_flag", UserConstants.ROLE_NORMAL)
            .eq(ObjectUtil.isNotNull(bo.roleId), "r.role_id", bo.roleId)
            .like(StringUtils.isNotBlank(bo.roleName), "r.role_name", bo.roleName)
            .eq(StringUtils.isNotBlank(bo.status), "r.status", bo.status)
            .like(StringUtils.isNotBlank(bo.roleKey), "r.role_key", bo.roleKey)
            .between(
                params["beginTime"] != null && params["endTime"] != null,
                "r.create_time", params["beginTime"], params["endTime"]
            )
            .orderByAsc("r.role_sort").orderByAsc("r.create_time")

        return wrapper
    }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    override fun selectRolesByUserId(userId: Long?): List<SysRoleVo> {
        val userRoles = baseMapper.selectRolePermissionByUserId(userId)
        val roles = selectRoleAll()
        for (role in roles) {
            for (userRole in userRoles) {
                if (role.roleId == userRole.roleId) {
                    role.flag = true
                    break
                }
            }
        }
        return roles
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    override fun selectRolePermissionByUserId(userId: Long): Set<String> {
        val perms = baseMapper.selectRolePermissionByUserId(userId)
        val permsSet: MutableSet<String> = HashSet()
        for (perm in perms) {
            if (ObjectUtil.isNotNull(perm)) {
                permsSet.addAll(StringUtilsExt.splitList(perm.roleKey!!.trim { it <= ' ' }))
            }
        }
        return permsSet
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    override fun selectRoleAll(): List<SysRoleVo> {
        return this.selectRoleList(SysRoleBo())
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    override fun selectRoleListByUserId(userId: Long?): List<Long> {
        return baseMapper.selectRoleListByUserId(userId)
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    override fun selectRoleById(roleId: Long?): SysRoleVo? {
        return baseMapper.selectRoleById(roleId)
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    override fun checkRoleNameUnique(role: SysRoleBo): Boolean {
        val exist = baseMapper.exists(
            KtQueryWrapper(SysRoleEntity::class.java)
                .eq(SysRoleEntity::roleName, role.roleName)
                .ne(ObjectUtil.isNotNull(role.roleId), SysRoleEntity::roleId, role.roleId)
        )
        return !exist
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    override fun checkRoleKeyUnique(role: SysRoleBo): Boolean {
        val exist = baseMapper.exists(
            KtQueryWrapper(SysRoleEntity::class.java)
                .eq(SysRoleEntity::roleKey, role.roleKey)
                .ne(ObjectUtil.isNotNull(role.roleId), SysRoleEntity::roleId, role.roleId)
        )
        return !exist
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    override fun checkRoleAllowed(role: SysRoleBo) {
        if (ObjectUtil.isNotNull(role.roleId) && LoginHelper.isSuperAdmin(role.roleId)) {
            throw ServiceException("不允许操作超级管理员角色")
        }
        val keys = arrayOf(TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY)
        // 新增不允许使用 管理员标识符
        if (ObjectUtil.isNull(role.roleId)
            && StringUtils.equalsAny(role.roleKey, *keys)
        ) {
            throw ServiceException("不允许使用系统内置管理员角色标识符!")
        }
        // 修改不允许修改 管理员标识符
        if (ObjectUtil.isNotNull(role.roleId)) {
            val sysRole = baseMapper.selectById(role.roleId)
            // 如果标识符不相等 判断为修改了管理员标识符
            if (!StringUtils.equals(sysRole.roleKey, role.roleKey)) {
                if (StringUtils.equalsAny(sysRole.roleKey, *keys)) {
                    throw ServiceException("不允许修改系统内置管理员角色标识符!")
                } else if (StringUtils.equalsAny(role.roleKey, *keys)) {
                    throw ServiceException("不允许使用系统内置管理员角色标识符!")
                }
            }
        }
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    override fun checkRoleDataScope(roleId: Long?) {
        if (ObjectUtil.isNull(roleId)) {
            return
        }
        if (LoginHelper.isSuperAdmin()) {
            return
        }
        val roles: List<SysRoleVo?> = this.selectRoleList(SysRoleBo().apply {
            this.roleId = roleId
        })
        if (CollUtil.isEmpty(roles)) {
            throw ServiceException("没有权限访问角色数据！")
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    override fun countUserRoleByRoleId(roleId: Long?): Long {
        return userRoleMapper.selectCount(KtQueryWrapper(SysUserRoleEntity::class.java).eq(SysUserRoleEntity::roleId, roleId))
    }

    /**
     * 新增保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insertRole(bo: SysRoleBo): Int {
        val role = convert(bo, SysRoleEntity::class.java)
        // 新增角色信息
        baseMapper.insert(role)
        bo.roleId = role.roleId
        return insertRoleMenu(bo)
    }

    /**
     * 修改保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun updateRole(bo: SysRoleBo): Int {
        val role = convert(bo, SysRoleEntity::class.java)
        // 修改角色信息
        baseMapper.updateById(role)
        // 删除角色与菜单关联
        roleMenuMapper.delete(KtQueryWrapper(SysRoleMenuEntity::class.java).eq(SysRoleMenuEntity::roleId, role.roleId))
        return insertRoleMenu(bo)
    }

    /**
     * 修改角色状态
     *
     * @param roleId 角色ID
     * @param status 角色状态
     * @return 结果
     */
    override fun updateRoleStatus(roleId: Long?, status: String?): Int {
        if (UserConstants.ROLE_DISABLE == status && this.countUserRoleByRoleId(roleId) > 0) {
            throw ServiceException("角色已分配，不能禁用!")
        }
        return baseMapper.update(
            null,
            KtUpdateWrapper(SysRoleEntity::class.java)
                .set(SysRoleEntity::status, status)
                .eq(SysRoleEntity::roleId, roleId)
        )
    }

    /**
     * 修改数据权限信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun authDataScope(bo: SysRoleBo): Int {
        val role = convert(bo, SysRoleEntity::class.java)
        // 修改角色信息
        baseMapper.updateById(role)
        // 删除角色与部门关联
        roleDeptMapper.delete(KtQueryWrapper(SysRoleDeptEntity::class.java).eq(SysRoleDeptEntity::roleId, role.roleId))
        // 新增角色和部门信息（数据权限）
        return insertRoleDept(bo)
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    private fun insertRoleMenu(role: SysRoleBo): Int {
        var rows = 1
        // 新增用户与角色管理
        val list: MutableList<SysRoleMenuEntity> = ArrayList()
        for (menuId in role.menuIds) {
            val rm = SysRoleMenuEntity()
            rm.roleId = role.roleId
            rm.menuId = menuId
            list.add(rm)
        }
        if (list.size > 0) {
            rows = if (roleMenuMapper.insertBatch(list)) list.size else 0
        }
        return rows
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    private fun insertRoleDept(role: SysRoleBo): Int {
        var rows = 1
        // 新增角色与部门（数据权限）管理
        val list: MutableList<SysRoleDeptEntity> = ArrayList()
        for (deptId in role.deptIds) {
            val rd = SysRoleDeptEntity()
            rd.roleId = role.roleId
            rd.deptId = deptId
            list.add(rd)
        }
        if (list.size > 0) {
            rows = if (roleDeptMapper.insertBatch(list)) list.size else 0
        }
        return rows
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun deleteRoleById(roleId: Long?): Int {
        // 删除角色与菜单关联
        roleMenuMapper.delete(KtQueryWrapper(SysRoleMenuEntity::class.java).eq(SysRoleMenuEntity::roleId, roleId))
        // 删除角色与部门关联
        roleDeptMapper.delete(KtQueryWrapper(SysRoleDeptEntity::class.java).eq(SysRoleDeptEntity::roleId, roleId))
        return baseMapper.deleteById(roleId)
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun deleteRoleByIds(roleIds: Array<Long>): Int {
        for (roleId in roleIds) {
            val role = baseMapper.selectById(roleId)
            checkRoleAllowed(BeanUtil.toBean(role, SysRoleBo::class.java))
            checkRoleDataScope(roleId)
            if (countUserRoleByRoleId(roleId) > 0) {
                throw ServiceException(String.format("%1\$s已分配，不能删除!", role.roleName))
            }
        }
        val ids = Arrays.asList(*roleIds)
        // 删除角色与菜单关联
        roleMenuMapper.delete(KtQueryWrapper(SysRoleMenuEntity::class.java).`in`(SysRoleMenuEntity::roleId, ids))
        // 删除角色与部门关联
        roleDeptMapper.delete(KtQueryWrapper(SysRoleDeptEntity::class.java).`in`(SysRoleDeptEntity::roleId, ids))
        return baseMapper.deleteBatchIds(ids)
    }

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    override fun deleteAuthUser(userRole: SysUserRoleEntity): Int {
        val rows = userRoleMapper.delete(
            KtQueryWrapper(SysUserRoleEntity::class.java)
                .eq(SysUserRoleEntity::roleId, userRole.roleId)
                .eq(SysUserRoleEntity::userId, userRole.userId)
        )
        if (rows > 0) {
            cleanOnlineUserByRole(userRole.roleId)
        }
        return rows
    }

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    override fun deleteAuthUsers(roleId: Long?, userIds: Array<Long>): Int {
        val rows = userRoleMapper.delete(
            KtQueryWrapper(SysUserRoleEntity::class.java)
                .eq(SysUserRoleEntity::roleId, roleId)
                .`in`(SysUserRoleEntity::userId, Arrays.asList(*userIds))
        )
        if (rows > 0) {
            cleanOnlineUserByRole(roleId)
        }
        return rows
    }

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    override fun insertAuthUsers(roleId: Long?, userIds: Array<Long>): Int {
        // 新增用户与角色管理
        var rows = 1

        val list = userIds.map {
            val ur = SysUserRoleEntity()
            ur.userId = it
            ur.roleId = roleId
            ur
        }
        if (CollUtil.isNotEmpty(list)) {
            rows = if (userRoleMapper.insertBatch(list)) list.size else 0
        }
        if (rows > 0) {
            cleanOnlineUserByRole(roleId)
        }
        return rows
    }

    override fun cleanOnlineUserByRole(roleId: Long?) {
        // 如果角色未绑定用户 直接返回
        val num = userRoleMapper.selectCount(KtQueryWrapper(SysUserRoleEntity::class.java).eq(SysUserRoleEntity::roleId, roleId))
        if (num == 0L) {
            return
        }
        val keys = StpUtil.searchTokenValue("", 0, -1, false)
        if (CollUtil.isEmpty(keys)) {
            return
        }
        // 角色关联的在线用户量过大会导致redis阻塞卡顿 谨慎操作
        keys.parallelStream().forEach { key: String? ->
            val token = StringUtils.substringAfterLast(key, ":")
            // 如果已经过期则跳过
            if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
                return@forEach
            }
            LoginHelper.getLoginUser(token)?.takeIf { it.roles.any { r -> r.roleId == roleId } }?.let {
                try {
                    StpUtil.logoutByTokenValue(token)
                } catch (ignored: NotLoginException) {
                }
            }
        }
    }
}
