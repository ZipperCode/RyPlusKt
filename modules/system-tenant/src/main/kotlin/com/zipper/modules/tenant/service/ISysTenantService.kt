package com.zipper.modules.tenant.service

import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.tenant.domain.param.SysTenantQueryParam
import com.zipper.modules.tenant.domain.param.SysTenantSaveParam
import com.zipper.modules.tenant.domain.vo.SysTenantVo

/**
 * 租户Service接口
 *
 * @author Michelle.Chung
 */
interface ISysTenantService {
    /**
     * 查询租户
     */
    fun queryById(id: Long?): SysTenantVo?

    /**
     * 基于租户ID查询租户
     */
    fun queryByTenantId(tenantId: String?): SysTenantVo?

    /**
     * 查询租户列表
     */
    fun queryPageList(param: SysTenantQueryParam, pageQuery: PageQuery): TableDataInfo<SysTenantVo>

    /**
     * 查询租户列表
     */
    fun queryList(): List<SysTenantVo>

    /**
     * 新增租户
     */
    fun insertByBo(param: SysTenantSaveParam): Boolean

    /**
     * 修改租户
     */
    fun updateByBo(param: SysTenantSaveParam): Boolean

    /**
     * 修改租户状态
     */
    fun updateTenantStatus(param: SysTenantSaveParam): Int

    /**
     * 校验租户是否允许操作
     */
    fun checkTenantAllowed(tenantId: String?)

    /**
     * 校验并批量删除租户信息
     */
    fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean

    /**
     * 校验企业名称是否唯一
     */
    fun checkCompanyNameUnique(param: SysTenantSaveParam): Boolean

    /**
     * 校验账号余额
     */
    fun checkAccountBalance(tenantId: String): Boolean

    /**
     * 校验有效期
     */
    fun checkExpireTime(tenantId: String): Boolean

    /**
     * 同步租户套餐
     */
    fun syncTenantPackage(tenantId: String, packageId: Long?): Boolean
}
