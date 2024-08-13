package com.zipper.framework.oss.factory

import com.zipper.framework.json.utils.JsonUtils
import com.zipper.framework.oss.constant.OssConstant
import com.zipper.framework.oss.core.OssClient
import com.zipper.framework.oss.exception.OssException
import com.zipper.framework.oss.properties.OssProperties
import com.zipper.framework.redis.utils.CacheUtils
import com.zipper.framework.redis.utils.RedisUtils
import com.zipper.framework.core.constant.CacheNames
import com.zipper.framework.core.ext.log
import java.util.concurrent.ConcurrentHashMap

/**
 * 文件上传Factory
 *
 * @author Lion Li
 */
object OssFactory {
    private val CLIENT_CACHE: MutableMap<String, OssClient> = ConcurrentHashMap<String, OssClient>()

    /**
     * 获取默认实例
     */
    fun instance(): OssClient {
        // 获取redis 默认类型
        val configKey: String? = RedisUtils.getCacheObject(OssConstant.DEFAULT_CONFIG_KEY)
        if (configKey.isNullOrEmpty()) {
            throw OssException("文件存储服务类型无法找到!")
        }
        return instance(configKey)
    }

    /**
     * 根据类型获取实例
     */
    @Synchronized
    fun instance(configKey: String): OssClient {
        val json: String = CacheUtils.get(CacheNames.SYS_OSS_CONFIG, configKey)
            ?: throw OssException("系统异常, '$configKey'配置信息不存在!")
        val properties: OssProperties = JsonUtils.parseObject(json, OssProperties::class.java)
            ?: throw OssException("系统异常, '$configKey'配置信息不存在!")
        // 使用租户标识避免多个租户相同key实例覆盖
        val key: String = properties.tenantId + ":" + configKey
        val client: OssClient? = CLIENT_CACHE[key]
        if (client == null) {
            CLIENT_CACHE[key] = OssClient(configKey, properties)
            log.info("创建OSS实例 key => {}", configKey)
            return CLIENT_CACHE[key]!!
        }
        // 配置不相同则重新构建
        if (!client.checkPropertiesSame(properties)) {
            CLIENT_CACHE[key] = OssClient(configKey, properties)
            log.info("重载OSS实例 key => {}", configKey)
            return CLIENT_CACHE[key]!!
        }
        return client
    }
}
