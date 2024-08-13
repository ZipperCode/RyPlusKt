package com.zipper.framework.redis.handler

import org.apache.commons.lang3.StringUtils
import org.redisson.api.NameMapper

/**
 * redis缓存key前缀处理
 *
 * @author ye
 * @date 2022/7/14 17:44
 * @since 4.3.0
 */
open class KeyPrefixHandler(keyPrefix: String) : NameMapper {
    //前缀为空 则返回空前缀
    private val keyPrefix = keyPrefix.ifBlank { "" }

    /**
     * 增加前缀
     */
    override fun map(name: String): String? {
        if (StringUtils.isBlank(name)) {
            return null
        }
        if (StringUtils.isNotBlank(keyPrefix) && !name.startsWith(keyPrefix)) {
            return keyPrefix + name
        }
        return name
    }

    /**
     * 去除前缀
     */
    override fun unmap(name: String): String? {
        if (StringUtils.isBlank(name)) {
            return null
        }
        if (StringUtils.isNotBlank(keyPrefix) && name.startsWith(keyPrefix)) {
            return name.substring(keyPrefix.length)
        }
        return name
    }
}
