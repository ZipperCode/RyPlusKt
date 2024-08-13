package com.zipper.modules.system.service.tenant

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysTenantPackageBo
import com.zipper.modules.system.domain.entity.SysTenantEntity
import com.zipper.modules.system.domain.entity.SysTenantPackageEntity
import com.zipper.modules.system.domain.vo.SysTenantPackageVo
import com.zipper.modules.system.mapper.SysTenantMapper
import com.zipper.modules.system.mapper.SysTenantPackageMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.MapstructUtils
import com.zipper.framework.core.utils.MapstructUtils.convert

/**
 * 租户套餐Service业务层处理
 *
 * @author Michelle.Chung
 */
@Service
class SysTenantPackageServiceImpl(private val baseMapper: SysTenantPackageMapper, private val tenantMapper: SysTenantMapper) :
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
    override fun queryPageList(bo: SysTenantPackageBo, pageQuery: PageQuery): TableDataInfo<SysTenantPackageVo> {
        val lqw = buildQueryWrapper(bo)
        val result = baseMapper.selectVoPage<Page<SysTenantPackageVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(result)
    }

    override fun selectList(): List<SysTenantPackageVo> {
        return baseMapper.selectVoList(
            KtQueryWrapper(SysTenantPackageEntity::class.java)
                .eq(SysTenantPackageEntity::status, TenantConstants.NORMAL)
        )
    }

    /**
     * 查询租户套餐列表
     */
    override fun queryList(bo: SysTenantPackageBo): List<SysTenantPackageVo> {
        val lqw = buildQueryWrapper(bo)
        return baseMapper.selectVoList(lqw)
    }

    private fun buildQueryWrapper(bo: SysTenantPackageBo): KtQueryWrapper<SysTenantPackageEntity> {
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
    override fun insertByBo(bo: SysTenantPackageBo): Boolean {
        val add = MapstructUtils.convert(bo, SysTenantPackageEntity::class.java)
        // 保存菜单id
        add.menuIds = bo.menuIds.joinToString(", ")
        val flag = baseMapper.insert(add) > 0
        if (flag) {
            bo.packageId = add.packageId
        }
        return flag
    }

    /**
     * 修改租户套餐
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun updateByBo(bo: SysTenantPackageBo): Boolean {
        val update = MapstructUtils.convert(bo, SysTenantPackageEntity::class.java)
        // 保存菜单id
        update.menuIds = bo.menuIds.joinToString(", ")
        return baseMapper.updateById(update) > 0
    }

    /**
     * 修改套餐状态
     *
     * @param bo 套餐信息
     * @return 结果
     */
    override fun updatePackageStatus(bo: SysTenantPackageBo): Int {
        val tenantPackage = convert(bo, SysTenantPackageEntity::class.java)
        return baseMapper.updateById(tenantPackage)
    }

    /**
     * 批量删除租户套餐
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean {
        if (isValid) {
            val exists = tenantMapper.exists(KtQueryWrapper(SysTenantEntity::class.java).`in`(SysTenantEntity::packageId, ids))
            if (exists) {
                throw ServiceException("租户套餐已被使用")
            }
        }
        return baseMapper.deleteBatchIds(ids) > 0
    }
}
