package com.zipper.framework.core.domain.event

/**
 * 创建租户事件
 * @param tenantId 租户id
 * @param tenantPackageId 租户套餐id
 * @param companyName 公司名称
 * @param username 创建系统用户名
 * @param password 密码
 * @param tenantPackageMenuIds 菜单id集合
 */
data class TenantCreateEvent(
    val tenantId: String,
    val companyName: String,
    val username: String,
    val password: String,
    val tenantPackageId: Long,
    val tenantPackageMenuIds: List<Long>
)