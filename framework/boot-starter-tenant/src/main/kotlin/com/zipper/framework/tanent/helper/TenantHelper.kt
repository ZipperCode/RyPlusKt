package com.zipper.framework.tanent.helper

import cn.dev33.satoken.context.SaHolder
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.convert.Convert
import cn.hutool.extra.spring.SpringUtil
import com.alibaba.ttl.TransmittableThreadLocal
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper
import com.zipper.framework.redis.utils.RedisUtils
import com.zipper.framework.satoken.utils.LoginHelper
import lombok.AccessLevel
import lombok.NoArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.StringUtils
import com.zipper.framework.core.constant.GlobalConstants
import java.util.function.Supplier

/**
 * 租户助手
 *
 * @author Lion Li
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
object TenantHelper {
    private const val DYNAMIC_TENANT_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "dynamicTenant"

    private val TEMP_DYNAMIC_TENANT: ThreadLocal<String> = TransmittableThreadLocal()

    /**
     * 租户功能是否启用
     */
    fun isEnable() = Convert.toBool(SpringUtil.getProperty("tenant.enable"), false)

    /**
     * 开启忽略租户(开启后需手动调用 [.disableIgnore] 关闭)
     */
    fun enableIgnore() {
        InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).build())
    }

    /**
     * 关闭忽略租户
     */
    fun disableIgnore() {
        InterceptorIgnoreHelper.clearIgnoreStrategy()
    }

    /**
     * 在忽略租户中执行
     *
     * @param handle 处理执行方法
     */
//    fun ignore(handle: Runnable) {
//        enableIgnore()
//        try {
//            handle.run()
//        } finally {
//            disableIgnore()
//        }
//    }

    /**
     * 在忽略租户中执行
     *
     * @param handle 处理执行方法
     */
    fun <T> ignore(handle: Supplier<T>): T {
        enableIgnore()
        try {
            return handle.get()
        } finally {
            disableIgnore()
        }
    }

    var dynamic: String?
        /**
         * 获取动态租户(一直有效 需要手动清理)
         *
         *
         * 如果为未登录状态下 那么只在当前线程内生效
         */
        get() {
            if (!isEnable()) {
                return null
            }
            if (!isLogin) {
                return TEMP_DYNAMIC_TENANT.get()
            }
            val cacheKey = DYNAMIC_TENANT_KEY + ":" + LoginHelper.getUserId()
            var tenantId = SaHolder.getStorage()[cacheKey] as? String
            if (StringUtils.isNotBlank(tenantId)) {
                return tenantId
            }
            tenantId = RedisUtils.getCacheObject(cacheKey) ?: ""
            SaHolder.getStorage()[cacheKey] = tenantId
            return tenantId
        }
        /**
         * 设置动态租户(一直有效 需要手动清理)
         *
         *
         * 如果为未登录状态下 那么只在当前线程内生效
         */
        set(tenantId) {
            if (!isEnable()) {
                return
            }
            if (!isLogin) {
                TEMP_DYNAMIC_TENANT.set(tenantId)
                return
            }
            val cacheKey = DYNAMIC_TENANT_KEY + ":" + LoginHelper.getUserId()
            RedisUtils.setCacheObject(cacheKey, tenantId)
            SaHolder.getStorage()[cacheKey] = tenantId
        }

    /**
     * 清除动态租户
     */
    fun clearDynamic() {
        if (!isEnable()) {
            return
        }
        if (!isLogin) {
            TEMP_DYNAMIC_TENANT.remove()
            return
        }
        val cacheKey = DYNAMIC_TENANT_KEY + ":" + LoginHelper.getUserId()
        RedisUtils.deleteObject(cacheKey)
        SaHolder.getStorage().delete(cacheKey)
    }

    /**
     * 在动态租户中执行
     *
     * @param handle 处理执行方法
     */
//    fun dynamic(tenantId: String, handle: Runnable) {
//        dynamic = tenantId
//        try {
//            handle.run()
//        } finally {
//            clearDynamic()
//        }
//    }

    /**
     * 在动态租户中执行
     *
     * @param handle 处理执行方法
     */
    fun <T> dynamic(tenantId: String?, handle: Supplier<T>): T {
        dynamic = tenantId
        try {
            return handle.get()
        } finally {
            clearDynamic()
        }
    }

    val tenantId: String
        /**
         * 获取当前租户id(动态租户优先)
         */
        get() {
            if (!isEnable()) {
                return ""
            }
            var tenantId = dynamic
            if (StringUtils.isBlank(tenantId)) {
                tenantId = LoginHelper.getTenantId()
            }
            return tenantId ?: ""
        }

    private val isLogin: Boolean
        get() {
            try {
                StpUtil.checkLogin()
                return true
            } catch (e: Exception) {
                return false
            }
        }
}
