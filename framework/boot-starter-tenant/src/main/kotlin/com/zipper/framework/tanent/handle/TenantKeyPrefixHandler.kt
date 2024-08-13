package com.zipper.framework.tanent.handle

import com.zipper.framework.redis.handler.KeyPrefixHandler
import com.zipper.framework.tanent.helper.TenantHelper
import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.StringUtils
import com.zipper.framework.core.constant.GlobalConstants
import com.zipper.framework.core.ext.log

/**
 * 多租户redis缓存key前缀处理
 *
 * @author Lion Li
 */
class TenantKeyPrefixHandler(keyPrefix: String) : KeyPrefixHandler(keyPrefix) {
    /**
     * 增加前缀
     */
    override fun map(name: String): String? {
        if (StringUtils.isBlank(name)) {
            return null
        }
        if (StringUtils.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.map(name)
        }
        val tenantId: String = TenantHelper.tenantId
        if (StringUtils.isBlank(tenantId)) {
            log.error("无法获取有效的租户id -> Null")
        }
        if (StringUtils.startsWith(name, tenantId + "")) {
            // 如果存在则直接返回
            return super.map(name)
        }
        return super.map("$tenantId:$name")
    }

    /**
     * 去除前缀
     */
    override fun unmap(name: String): String? {
        val unmap = super.unmap(name)
        if (StringUtils.isBlank(unmap)) {
            return null
        }
        if (StringUtils.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.unmap(name)
        }
        val tenantId: String = TenantHelper.tenantId
        if (StringUtils.isBlank(tenantId)) {
            log.error("无法获取有效的租户id -> Null")
        }
        if (StringUtils.startsWith(unmap, tenantId + "")) {
            // 如果存在则删除
            return unmap!!.substring("$tenantId:".length)
        }
        return unmap
    }
}
