package com.zipper.framework.core.modules

import com.zipper.framework.core.utils.SpringUtilExt

interface ITenantApi {

    companion object {
        const val IMPL = "tenantApiService"

        fun getImpl(): ITenantApi? {
            return SpringUtilExt.getBeanByNameOrNull<ITenantApi>(IMPL)
        }
    }

    /**
     * 获取当前租户的菜单id
     */
    fun getPackageMenuIds(packageId: Long): Pair<Boolean, List<Long>>

    /**
     * 校验账号余额
     */
    fun checkAccountBalance(tenantId: String, invokeUserCount: () -> Long): Boolean
}