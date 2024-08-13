package com.zipper.server.web.domain.vo

/**
 * 登录租户对象
 *
 * @author Michelle.Chung
 */
class LoginTenantVo {
    /**
     * 租户开关
     */
    var tenantEnabled: Boolean = false

    /**
     * 租户对象列表
     */
    var voList: List<TenantListVo>? = null
}
