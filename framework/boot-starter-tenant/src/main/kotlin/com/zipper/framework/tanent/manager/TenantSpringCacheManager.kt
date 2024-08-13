package com.zipper.framework.tanent.manager

import com.zipper.framework.redis.manager.PlusSpringCacheManager
import com.zipper.framework.tanent.helper.TenantHelper
import org.apache.commons.lang3.StringUtils
import org.springframework.cache.Cache
import com.zipper.framework.core.constant.GlobalConstants


/**
 * 重写 cacheName 处理方法 支持多租户
 *
 * @author Lion Li
 */
class TenantSpringCacheManager : PlusSpringCacheManager() {
    override fun getCache(name: String): Cache? {
        if (StringUtils.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.getCache(name)
        }
        val tenantId = TenantHelper.tenantId
        if (StringUtils.startsWith(name, tenantId)) {
            // 如果存在则直接返回
            return super.getCache(name)
        }
        return super.getCache("$tenantId:$name")
    }
}
