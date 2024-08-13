package com.zipper.modules.system.service.tenant

import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysTenantPackageBo
import com.zipper.modules.system.domain.vo.SysTenantPackageVo


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
    fun queryPageList(bo: SysTenantPackageBo, pageQuery: PageQuery): TableDataInfo<SysTenantPackageVo>

    /**
     * 查询租户套餐已启用列表
     */
    fun selectList(): List<SysTenantPackageVo>

    /**
     * 查询租户套餐列表
     */
    fun queryList(bo: SysTenantPackageBo): List<SysTenantPackageVo>

    /**
     * 新增租户套餐
     */
    fun insertByBo(bo: SysTenantPackageBo): Boolean

    /**
     * 修改租户套餐
     */
    fun updateByBo(bo: SysTenantPackageBo): Boolean

    /**
     * 修改套餐状态
     */
    fun updatePackageStatus(bo: SysTenantPackageBo): Int

    /**
     * 校验并批量删除租户套餐信息
     */
    fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean
}
