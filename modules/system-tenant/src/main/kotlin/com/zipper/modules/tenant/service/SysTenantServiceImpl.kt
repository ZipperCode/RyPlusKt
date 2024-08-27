package com.zipper.modules.tenant.service

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import cn.hutool.core.util.RandomUtil
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.core.constant.CacheNames
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.domain.event.TenantCreateEvent
import com.zipper.framework.core.domain.event.TenantSyncPackageEvent
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.modules.ITenantApi
import com.zipper.framework.core.utils.MapstructUtils
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.framework.core.utils.StringUtilsExt
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.core.eqIfPresent
import com.zipper.framework.mybatis.core.likeIfPresent
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.tenant.domain.entity.SysTenantEntity
import com.zipper.modules.tenant.domain.param.SysTenantQueryParam
import com.zipper.modules.tenant.domain.param.SysTenantSaveParam
import com.zipper.modules.tenant.domain.vo.SysTenantVo
import com.zipper.modules.tenant.mapper.SysTenantMapper
import com.zipper.modules.tenant.mapper.SysTenantPackageMapper
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.function.Consumer

/**
 * 租户Service业务层处理
 *
 * @author Michelle.Chung
 */
@Service
class SysTenantServiceImpl(
    private val baseMapper: SysTenantMapper,
    private val tenantPackageMapper: SysTenantPackageMapper,
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
    override fun queryPageList(param: SysTenantQueryParam, pageQuery: PageQuery): TableDataInfo<SysTenantVo> {
        val lqw = MybatisKt.ktQuery<SysTenantEntity>()
            .eqIfPresent(SysTenantEntity::tenantId, param.tenantId)
            .likeIfPresent(SysTenantEntity::contactUserName, param.contactUserName)
            .likeIfPresent(SysTenantEntity::contactPhone, param.contactPhone)
            .likeIfPresent(SysTenantEntity::companyName, param.companyName)
            .eqIfPresent(SysTenantEntity::status, param.status)
        val result = baseMapper.selectVoPage<Page<SysTenantVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(result)
    }

    /**
     * 查询租户列表
     */
    override fun queryList(): List<SysTenantVo> {
        return baseMapper.selectVoList(QueryWrapper())
    }

    /**
     * 新增租户
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insertByBo(param: SysTenantSaveParam): Boolean {
        val add = MapstructUtils.convert(param, SysTenantEntity::class.java)

        // 获取所有租户编号
        val tenantIds = baseMapper.selectList(
            MybatisKt.ktQuery<SysTenantEntity>()
                .select(SysTenantEntity::tenantId)
        ).filterNotNull().map { it.toString() }
        val tenantId = generateTenantId(tenantIds)
        add.tenantId = tenantId
        val flag = baseMapper.insert(add) > 0
        if (!flag) {
            throw ServiceException("创建租户失败")
        }
        // 获取租户套餐
        val tenantPackage = tenantPackageMapper.selectById(param.packageId)
        if (ObjectUtil.isNull(tenantPackage)) {
            throw ServiceException("套餐不存在")
        }
        // 获取套餐菜单id
        val menuIds: List<Long> = StringUtilsExt.splitTo(tenantPackage.menuIds, Convert::toLong)

        SpringUtilExt.context().publishEvent(
            TenantCreateEvent(
                tenantId,
                param.requireCompanyName,
                param.requireUsername,
                param.requirePassword,
                tenantPackage.requirePackageId,
                menuIds
            )
        )

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
     * 修改租户
     */
    @CacheEvict(cacheNames = [CacheNames.SYS_TENANT], key = "#bo.tenantId")
    override fun updateByBo(param: SysTenantSaveParam): Boolean {
        val tenant = convert(param, SysTenantEntity::class.java)
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
    @CacheEvict(cacheNames = [CacheNames.SYS_TENANT], key = "#param.tenantId")
    override fun updateTenantStatus(param: SysTenantSaveParam): Int {
        val tenant = convert(param, SysTenantEntity::class.java)
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
    override fun checkCompanyNameUnique(param: SysTenantSaveParam): Boolean {
        val exist = baseMapper.exists(
            KtQueryWrapper(SysTenantEntity::class.java)
                .eq(SysTenantEntity::companyName, param.companyName)
                .ne(ObjectUtil.isNotNull(param.tenantId), SysTenantEntity::tenantId, param.tenantId)
        )
        return !exist
    }

    /**
     * 校验账号余额
     */
    override fun checkAccountBalance(tenantId: String): Boolean {
//        val tenant = SpringUtilExt.getAopProxy(this).queryByTenantId(tenantId)
//        // 如果余额为-1代表不限制
//        if (tenant?.accountCount == -1L) {
//            return true
//        }
//        val userNumber = userMapper.selectCount(KtQueryWrapper(SysUserEntity::class.java))
//        // 如果余额大于0代表还有可用名额
//        return (tenant?.accountCount ?: 0) - userNumber > 0
        return true
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
    override fun syncTenantPackage(tenantId: String, packageId: Long?): Boolean {
        val tenantPackage = tenantPackageMapper.selectById(packageId)
        SpringUtilExt.context().publishEvent(
            TenantSyncPackageEvent(
                tenantId,
                StringUtilsExt.splitTo(tenantPackage.menuIds, Convert::toLong)
            )
        )
        return true
    }
}
