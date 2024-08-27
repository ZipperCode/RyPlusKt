package com.zipper.framework.core.domain.event

data class TenantSyncPackageEvent(
    val tenantId: String,
    val menuIds: List<Long>
) {
}