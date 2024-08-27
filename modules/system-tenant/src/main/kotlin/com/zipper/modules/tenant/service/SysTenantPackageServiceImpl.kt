package com.zipper.modules.tenant.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.MapstructUtils
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.modules.tenant.domain.entity.SysTenantEntity
import com.zipper.modules.tenant.domain.entity.SysTenantPackageEntity
import com.zipper.modules.tenant.domain.param.SysTenantPackageQueryParam
import com.zipper.modules.tenant.domain.param.SysTenantPackageSaveParam
import com.zipper.modules.tenant.domain.vo.SysTenantPackageVo
import com.zipper.modules.tenant.mapper.SysTenantMapper
import com.zipper.modules.tenant.mapper.SysTenantPackageMapper

/**
 * 租户套餐Service业务层处理
 *
 * @author Michelle.Chung
 */
@Service
class SysTenantPackageServiceImpl(
    private val baseMapper: SysTenantPackageMapper,
    private val tenantMapper: SysTenantMapper
) :
    ISysTenantPackageService {
    /**
     * 查询租户套餐
     */
    override fun queryById(packageId: Long?): SysTenantPackageVo? {
        return baseMapper.selectVoById(packageId)
    }

    /**
     * 查询租户套餐列表
     */
    override fun queryPageList(param: SysTenantPackageQueryParam, pageQuery: PageQuery): TableDataInfo<SysTenantPackageVo> {
        val lqw = buildQueryWrapper(param)
        val result = baseMapper.selectVoPage<Page<SysTenantPackageVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(result)
    }

    override fun selectList(): List<SysTenantPackageVo> {
        return baseMapper.selectVoList(
            MybatisKt.ktQuery<SysTenantPackageEntity>()
                .eq(SysTenantPackageEntity::status, TenantConstants.NORMAL)
        )
    }

    /**
     * 查询租户套餐列表
     */
    override fun queryList(param: SysTenantPackageQueryParam): List<SysTenantPackageVo> {
        val lqw = buildQueryWrapper(param)
        return baseMapper.selectVoList(lqw)
    }

    private fun buildQueryWrapper(bo: SysTenantPackageQueryParam): KtQueryWrapper<SysTenantPackageEntity> {
        val lqw = KtQueryWrapper(SysTenantPackageEntity::class.java)
        lqw.like(StringUtils.isNotBlank(bo.packageName), SysTenantPackageEntity::packageName, bo.packageName)
        lqw.eq(StringUtils.isNotBlank(bo.status), SysTenantPackageEntity::status, bo.status)
        lqw.orderByAsc(SysTenantPackageEntity::packageId)
        return lqw
    }

    /**
     * 新增租户套餐
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insert(param: SysTenantPackageSaveParam): Boolean {
        val add = MapstructUtils.convert(param, SysTenantPackageEntity::class.java)
        // 保存菜单id
        add.menuIds = param.menuIds.joinToString(", ")
        val flag = baseMapper.insert(add) > 0
        if (flag) {
            param.packageId = add.packageId
        }
        return flag
    }

    /**
     * 修改租户套餐
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun update(param: SysTenantPackageSaveParam): Boolean {
        val update = MapstructUtils.convert(param, SysTenantPackageEntity::class.java)
        // 保存菜单id
        update.menuIds = param.menuIds.joinToString(", ")
        return baseMapper.updateById(update) > 0
    }

    /**
     * 修改套餐状态
     *
     * @param param 套餐信息
     * @return 结果
     */
    override fun updatePackageStatus(param: SysTenantPackageSaveParam): Int {
        val tenantPackage = convert(param, SysTenantPackageEntity::class.java)
        return baseMapper.updateById(tenantPackage)
    }

    /**
     * 批量删除租户套餐
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean {
        if (isValid) {
            val exists = tenantMapper.exists(
                KtQueryWrapper(SysTenantEntity::class.java)
                    .`in`(SysTenantEntity::packageId, ids)
            )
            if (exists) {
                throw ServiceException("租户套餐已被使用")
            }
        }
        return baseMapper.deleteBatchIds(ids) > 0
    }
}
