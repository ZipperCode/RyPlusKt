package com.zipper.modules.system.service.tenant

import cn.dev33.satoken.secure.BCrypt
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import cn.hutool.core.util.RandomUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysTenantBo
import com.zipper.modules.system.domain.entity.*
import com.zipper.modules.system.domain.vo.SysTenantVo
import com.zipper.modules.system.mapper.*
import org.apache.commons.lang3.StringUtils
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.zipper.framework.core.constant.CacheNames
import com.zipper.framework.core.constant.Constants
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.framework.core.utils.StringUtilsExt
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

/**
 * 租户Service业务层处理
 *
 * @author Michelle.Chung
 */
@Service
class SysTenantServiceImpl(
    private val baseMapper: SysTenantMapper,
    private val tenantPackageMapper: SysTenantPackageMapper,
    private val userMapper: SysUserMapper,
    private val deptMapper: SysDeptMapper,
    private val roleMapper: SysRoleMapper,
    private val roleMenuMapper: SysRoleMenuMapper,
    private val roleDeptMapper: SysRoleDeptMapper,
    private val userRoleMapper: SysUserRoleMapper,
    private val dictTypeMapper: SysDictTypeMapper,
    private val dictDataMapper: SysDictDataMapper,
    private val configMapper: SysConfigMapper
) : ISysTenantService {
    /**
     * 查询租户
     */
    override fun queryById(id: Long?): SysTenantVo? {
        return baseMapper.selectVoById(id)
    }

    /**
     * 基于租户ID查询租户
     */
    @Cacheable(cacheNames = [CacheNames.SYS_TENANT], key = "#tenantId")
    override fun queryByTenantId(tenantId: String?): SysTenantVo? {
        return baseMapper.selectVoOne(KtQueryWrapper(SysTenantEntity::class.java).eq(SysTenantEntity::tenantId, tenantId))
    }

    /**
     * 查询租户列表
     */
    override fun queryPageList(bo: SysTenantBo, pageQuery: PageQuery): TableDataInfo<SysTenantVo> {
        val lqw = buildQueryWrapper(bo)
        val result = baseMapper.selectVoPage<Page<SysTenantVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(result)
    }

    /**
     * 查询租户列表
     */
    override fun queryList(bo: SysTenantBo): List<SysTenantVo> {
        val lqw = buildQueryWrapper(bo)
        return baseMapper.selectVoList(lqw)
    }

    private fun buildQueryWrapper(bo: SysTenantBo): KtQueryWrapper<SysTenantEntity> {
        val lqw = KtQueryWrapper(SysTenantEntity::class.java)
        lqw.eq(StringUtils.isNotBlank(bo.tenantId), SysTenantEntity::tenantId, bo.tenantId)
        lqw.like(StringUtils.isNotBlank(bo.contactUserName), SysTenantEntity::contactUserName, bo.contactUserName)
        lqw.eq(StringUtils.isNotBlank(bo.contactPhone), SysTenantEntity::contactPhone, bo.contactPhone)
        lqw.like(StringUtils.isNotBlank(bo.companyName), SysTenantEntity::companyName, bo.companyName)
        lqw.eq(StringUtils.isNotBlank(bo.licenseNumber), SysTenantEntity::licenseNumber, bo.licenseNumber)
        lqw.eq(StringUtils.isNotBlank(bo.address), SysTenantEntity::address, bo.address)
        lqw.eq(StringUtils.isNotBlank(bo.intro), SysTenantEntity::intro, bo.intro)
        lqw.like(StringUtils.isNotBlank(bo.domain), SysTenantEntity::domain, bo.domain)
        lqw.eq(bo.packageId != null, SysTenantEntity::packageId, bo.packageId)
        lqw.eq(bo.expireTime != null, SysTenantEntity::expireTime, bo.expireTime)
        lqw.eq(bo.accountCount != null, SysTenantEntity::accountCount, bo.accountCount)
        lqw.eq(StringUtils.isNotBlank(bo.status), SysTenantEntity::status, bo.status)
        lqw.orderByAsc(SysTenantEntity::id)
        return lqw
    }

    /**
     * 新增租户
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insertByBo(bo: SysTenantBo): Boolean {
        val add = convert(bo, SysTenantEntity::class.java)

        // 获取所有租户编号
        val tenantIds = baseMapper.selectObjs(
            KtQueryWrapper(SysTenantEntity::class.java).select(SysTenantEntity::tenantId), Function { x: Any? -> Convert.toStr(x) })
        val tenantId = generateTenantId(tenantIds)
        add.tenantId = tenantId
        val flag = baseMapper.insert(add) > 0
        if (!flag) {
            throw ServiceException("创建租户失败")
        }
        bo.id = add.id

        // 根据套餐创建角色
        val roleId = createTenantRole(tenantId, bo.packageId)

        // 创建部门: 公司名是部门名称
        val dept = SysDeptEntity()
        dept.tenantId = tenantId
        dept.deptName = bo.companyName
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
        user.tenantId = tenantId
        user.userName = bo.username
        user.nickName = bo.username
        user.password = BCrypt.hashpw(bo.password)
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
            dictType.tenantId = tenantId
        }
        for (dictData in dictDataList) {
            dictData.dictCode = null
            dictData.tenantId = tenantId
        }
        dictTypeMapper.insertBatch(dictTypeList)
        dictDataMapper.insertBatch(dictDataList)

        val sysConfigList = configMapper.selectList(
            KtQueryWrapper(SysConfigEntity::class.java).eq(SysConfigEntity::tenantId, defaultTenantId)
        )
        for (config in sysConfigList) {
            config.configId = null
            config.tenantId = tenantId
        }
        configMapper.insertBatch(sysConfigList)
        return true
    }

    /**
     * 生成租户id
     *
     * @param tenantIds 已有租户id列表
     * @return 租户id
     */
    private fun generateTenantId(tenantIds: List<String>): String {
        // 随机生成6位
        val numbers = RandomUtil.randomNumbers(6)
        // 判断是否存在，如果存在则重新生成
        if (tenantIds.contains(numbers)) {
            generateTenantId(tenantIds)
        }
        return numbers
    }

    /**
     * 根据租户菜单创建租户角色
     *
     * @param tenantId  租户编号
     * @param packageId 租户套餐id
     * @return 角色id
     */
    private fun createTenantRole(tenantId: String, packageId: Long?): Long {
        // 获取租户套餐
        val tenantPackage = tenantPackageMapper.selectById(packageId)
        if (ObjectUtil.isNull(tenantPackage)) {
            throw ServiceException("套餐不存在")
        }
        // 获取套餐菜单id
        val menuIds: List<Long> = StringUtilsExt.splitTo(tenantPackage.menuIds, Convert::toLong)

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
        val roleMenus = menuIds.map {
            val roleMenu = SysRoleMenuEntity()
            roleMenu.roleId = roleId
            roleMenu.menuId = it
            roleMenu
        }
        roleMenuMapper.insertBatch(roleMenus)

        return roleId
    }

    /**
     * 修改租户
     */
    @CacheEvict(cacheNames = [CacheNames.SYS_TENANT], key = "#bo.tenantId")
    override fun updateByBo(bo: SysTenantBo): Boolean {
        val tenant = convert(bo, SysTenantEntity::class.java)
        tenant.tenantId = null
        tenant.packageId = null
        return baseMapper.updateById(tenant) > 0
    }

    /**
     * 修改租户状态
     *
     * @param bo 租户信息
     * @return 结果
     */
    @CacheEvict(cacheNames = [CacheNames.SYS_TENANT], key = "#bo.tenantId")
    override fun updateTenantStatus(bo: SysTenantBo): Int {
        val tenant = convert(bo, SysTenantEntity::class.java)
        return baseMapper.updateById(tenant)
    }

    /**
     * 校验租户是否允许操作
     *
     * @param tenantId 租户ID
     */
    override fun checkTenantAllowed(tenantId: String?) {
        if (ObjectUtil.isNotNull(tenantId) && TenantConstants.DEFAULT_TENANT_ID == tenantId) {
            throw ServiceException("不允许操作管理租户")
        }
    }

    /**
     * 批量删除租户
     */
    @CacheEvict(cacheNames = [CacheNames.SYS_TENANT], allEntries = true)
    override fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
            if (ids.contains(TenantConstants.SUPER_ADMIN_ID)) {
                throw ServiceException("超管租户不能删除")
            }
        }
        return baseMapper.deleteBatchIds(ids) > 0
    }

    /**
     * 校验企业名称是否唯一
     */
    override fun checkCompanyNameUnique(bo: SysTenantBo): Boolean {
        val exist = baseMapper.exists(
            KtQueryWrapper(SysTenantEntity::class.java)
                .eq(SysTenantEntity::companyName, bo.companyName)
                .ne(ObjectUtil.isNotNull(bo.tenantId), SysTenantEntity::tenantId, bo.tenantId)
        )
        return !exist
    }

    /**
     * 校验账号余额
     */
    override fun checkAccountBalance(tenantId: String): Boolean {
        val tenant = SpringUtilExt.getAopProxy(this).queryByTenantId(tenantId)
        // 如果余额为-1代表不限制
        if (tenant?.accountCount == -1L) {
            return true
        }
        val userNumber = userMapper.selectCount(KtQueryWrapper(SysUserEntity::class.java))
        // 如果余额大于0代表还有可用名额
        return (tenant?.accountCount ?: 0) - userNumber > 0
    }

    /**
     * 校验有效期
     */
    override fun checkExpireTime(tenantId: String): Boolean {
        val tenant = SpringUtilExt.getAopProxy(this).queryByTenantId(tenantId)
        // 如果未设置过期时间代表不限制
        if (ObjectUtil.isNull(tenant?.expireTime)) {
            return true
        }
        // 如果当前时间在过期时间之前则通过
        return Date().before(tenant?.expireTime)
    }

    /**
     * 同步租户套餐
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun syncTenantPackage(tenantId: String?, packageId: Long?): Boolean {
        val tenantPackage = tenantPackageMapper.selectById(packageId)
        val roles = roleMapper.selectList(
            KtQueryWrapper(SysRoleEntity::class.java).eq(SysRoleEntity::tenantId, tenantId)
        )
        val roleIds: MutableList<Long> = ArrayList(roles.size - 1)
        val menuIds: List<Long> = StringUtilsExt.splitTo(tenantPackage.menuIds, Convert::toLong)
        roles.forEach(Consumer { item: SysRoleEntity ->
            if (TenantConstants.TENANT_ADMIN_ROLE_KEY == item.roleKey) {
                val roleMenus: MutableList<SysRoleMenuEntity> = ArrayList(menuIds.size)
                menuIds.forEach(Consumer { menuId: Long? ->
                    val roleMenu = SysRoleMenuEntity()
                    roleMenu.roleId = item.roleId
                    roleMenu.menuId = menuId
                    roleMenus.add(roleMenu)
                })
                roleMenuMapper.delete(KtQueryWrapper(SysRoleMenuEntity::class.java).eq(SysRoleMenuEntity::roleId, item.roleId))
                roleMenuMapper.insertBatch(roleMenus)
            } else {
                roleIds.add(item.roleId)
            }
        })
        if (roleIds.isNotEmpty()) {
            roleMenuMapper.delete(
                KtQueryWrapper(SysRoleMenuEntity::class.java).`in`(SysRoleMenuEntity::roleId, roleIds)
                    .notIn(menuIds.isNotEmpty(), SysRoleMenuEntity::menuId, menuIds)
            )
        }
        return true
    }
}
