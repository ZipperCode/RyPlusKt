/**
 * Copyright (c) 2013-2021 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zipper.framework.redis.manager

import com.zipper.framework.redis.utils.RedisUtils
import org.redisson.api.RMap
import org.redisson.api.RMapCache
import org.redisson.spring.cache.CacheConfig
import org.redisson.spring.cache.RedissonCache
import org.springframework.boot.convert.DurationStyle
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.transaction.TransactionAwareCacheDecorator
import org.springframework.util.StringUtils
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * A [CacheManager] implementation
 * backed by Redisson instance.
 *
 *
 * 修改 RedissonSpringCacheManager 源码
 * 重写 cacheName 处理方法 支持多参数
 *
 * @author Nikita Koksharov
 */
open class PlusSpringCacheManager : CacheManager {
    private var dynamic = true

    private var allowNullValues = true

    private var transactionAware = true

    var configMap: MutableMap<String, CacheConfig> = ConcurrentHashMap<String, CacheConfig>()
    var instanceMap: ConcurrentMap<String, Cache> = ConcurrentHashMap()

    /**
     * Defines possibility of storing `null` values.
     *
     *
     * Default is `true`
     *
     * @param allowNullValues stores if `true`
     */
    fun setAllowNullValues(allowNullValues: Boolean) {
        this.allowNullValues = allowNullValues
    }

    /**
     * Defines if cache aware of Spring-managed transactions.
     * If `true` put/evict operations are executed only for successful transaction in after-commit phase.
     *
     *
     * Default is `false`
     *
     * @param transactionAware cache is transaction aware if `true`
     */
    fun setTransactionAware(transactionAware: Boolean) {
        this.transactionAware = transactionAware
    }

    /**
     * Defines 'fixed' cache names.
     * A new cache instance will not be created in dynamic for non-defined names.
     *
     *
     * `null` parameter setups dynamic mode
     *
     * @param names of caches
     */
    fun setCacheNames(names: Collection<String>?) {
        if (names != null) {
            for (name in names) {
                getCache(name)
            }
            dynamic = false
        } else {
            dynamic = true
        }
    }

    /**
     * Set cache config mapped by cache name
     *
     * @param config object
     */
    fun setConfig(config: MutableMap<String, CacheConfig>) {
        this.configMap = config
    }

    protected fun createDefaultConfig(): CacheConfig {
        return CacheConfig()
    }

    override fun getCache(name: String): Cache? {
        // 重写 cacheName 支持多参数
        val array = StringUtils.delimitedListToStringArray(name, "#")
        val newName = array.first()

        val cache = instanceMap[newName]
        if (cache != null) {
            return cache
        }
        if (!dynamic) {
            return null
        }

        var config = configMap[name]
        if (config == null) {
            config = createDefaultConfig()
            configMap[name] = config
        }

        if (array.size > 1) {
            config.ttl = DurationStyle.detectAndParse(array[1]).toMillis()
        }
        if (array.size > 2) {
            config.maxIdleTime = DurationStyle.detectAndParse(array[2]).toMillis()
        }
        if (array.size > 3) {
            config.maxSize = array[3].toInt()
        }

        if (config.maxIdleTime == 0L && config.ttl == 0L && config.maxSize == 0) {
            return createMap(name, config)
        }

        return createMapCache(name, config)
    }

    private fun createMap(name: String, config: CacheConfig?): Cache {
        val map: RMap<Any, Any> = RedisUtils.client.getMap(name)

        var cache: Cache = RedissonCache(map, allowNullValues)
        if (transactionAware) {
            cache = TransactionAwareCacheDecorator(cache)
        }
        val oldCache = instanceMap.putIfAbsent(name, cache)
        if (oldCache != null) {
            cache = oldCache
        }
        return cache
    }

    private fun createMapCache(name: String, config: CacheConfig?): Cache {
        val map: RMapCache<Any, Any> = RedisUtils.client.getMapCache(name)

        var cache: Cache = RedissonCache(map, config, allowNullValues)
        if (transactionAware) {
            cache = TransactionAwareCacheDecorator(cache)
        }
        val oldCache = instanceMap.putIfAbsent(name, cache)
        if (oldCache != null) {
            cache = oldCache
        } else {
            map.setMaxSize(config!!.maxSize)
        }
        return cache
    }

    override fun getCacheNames(): Collection<String> {
        return Collections.unmodifiableSet(configMap.keys)
    }
}
