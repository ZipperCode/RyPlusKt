package com.zipper.modules.tenant.service

import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.tenant.domain.param.SysTenantPackageQueryParam
import com.zipper.modules.tenant.domain.param.SysTenantPackageSaveParam
import com.zipper.modules.tenant.domain.vo.SysTenantPackageVo


/**
 * 租户套餐Service接口
 *
 * @author Michelle.Chung
 */
interface ISysTenantPackageService {
    /**
     * 查询租户套餐
     */
    fun queryById(packageId: Long?): SysTenantPackageVo?

    /**
     * 查询租户套餐列表
     */
    fun queryPageList(param: SysTenantPackageQueryParam, pageQuery: PageQuery): TableDataInfo<SysTenantPackageVo>

    /**
     * 查询租户套餐已启用列表
     */
    fun selectList(): List<SysTenantPackageVo>

    /**
     * 查询租户套餐列表
     */
    fun queryList(param: SysTenantPackageQueryParam): List<SysTenantPackageVo>

    /**
     * 新增租户套餐
     */
    fun insert(param: SysTenantPackageSaveParam): Boolean

    /**
     * 修改租户套餐
     */
    fun update(param: SysTenantPackageSaveParam): Boolean

    /**
     * 修改套餐状态
     */
    fun updatePackageStatus(param: SysTenantPackageSaveParam): Int

    /**
     * 校验并批量删除租户套餐信息
     */
    fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean
}
