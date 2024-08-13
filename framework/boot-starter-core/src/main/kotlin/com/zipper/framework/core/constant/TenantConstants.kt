package com.zipper.framework.core.constant

/**
 * 租户常量信息
 *
 * @author Lion Li
 */
object TenantConstants {
    /**
     * 租户正常状态
     */
    const val NORMAL: String = "0"

    /**
     * 租户封禁状态
     */
    const val DISABLE: String = "1"

    /**
     * 超级管理员ID
     */
    const val SUPER_ADMIN_ID: Long = 1L

    /**
     * 超级管理员角色 roleKey
     */
    const val SUPER_ADMIN_ROLE_KEY: String = "superadmin"

    /**
     * 租户管理员角色 roleKey
     */
    const val TENANT_ADMIN_ROLE_KEY: String = "admin"

    /**
     * 租户管理员角色名称
     */
    const val TENANT_ADMIN_ROLE_NAME: String = "管理员"

    /**
     * 默认租户ID
     */
    const val DEFAULT_TENANT_ID: String = "000000"
}
