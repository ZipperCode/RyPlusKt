package com.zipper.framework.tanent.core

/**
 * 租户实现
 */
interface TenantMixin {
    /**
     * 租户编号
     */
    var tenantId: String?
}