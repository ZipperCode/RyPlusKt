package com.zipper.framework.core.constant

/**
 * 缓存的key 常量
 *
 * @author Lion Li
 */
object CacheConstants {
    /**
     * 在线用户 redis key
     */
    const val ONLINE_TOKEN_KEY: String = "online_tokens:"

    /**
     * 参数管理 cache key
     */
    const val SYS_CONFIG_KEY: String = "sys_config:"

    /**
     * 字典管理 cache key
     */
    const val SYS_DICT_KEY: String = "sys_dict:"
}
