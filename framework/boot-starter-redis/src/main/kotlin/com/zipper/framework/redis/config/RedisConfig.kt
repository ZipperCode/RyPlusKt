package com.zipper.framework.redis.config

import cn.hutool.core.util.ObjectUtil
import com.baomidou.lock.spring.boot.autoconfigure.LockAutoConfiguration
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.zipper.framework.redis.config.properties.RedissonProperties
import com.zipper.framework.redis.handler.KeyPrefixHandler
import com.zipper.framework.redis.manager.PlusSpringCacheManager
import jakarta.annotation.Resource
import lombok.extern.slf4j.Slf4j
import org.redisson.client.codec.StringCodec
import org.redisson.codec.CompositeCodec
import org.redisson.codec.TypedJsonJacksonCodec
import org.redisson.config.Config
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import com.zipper.framework.core.ext.log
import org.redisson.spring.starter.RedissonAutoConfiguration

/**
 * redis配置
 *
 * @author Lion Li
 */
@AutoConfiguration(before = [LockAutoConfiguration::class])
@EnableCaching
@EnableConfigurationProperties(RedissonProperties::class)
class RedisConfig {
    @Resource
    private lateinit var redissonProperties: RedissonProperties

    @Resource
    private lateinit var objectMapper: ObjectMapper

    @Bean
    fun redissonCustomizer(): RedissonAutoConfigurationCustomizer {
        return RedissonAutoConfigurationCustomizer { config: Config ->
            val om = objectMapper.copy()
            om.registerModules(kotlinModule())
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            // 指定序列化输入的类型，类必须是非final修饰的。序列化时将对象全类名一起保存下来
            om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,  JsonTypeInfo.As.PROPERTY)
            val jsonCodec = TypedJsonJacksonCodec(Any::class.java, om)
            // 组合序列化 key 使用 String 内容使用通用 json 格式
            val codec = CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec)
            config.setThreads(redissonProperties.threads)
                .setNettyThreads(redissonProperties.nettyThreads) // 缓存 Lua 脚本 减少网络传输(redisson 大部分的功能都是基于 Lua 脚本实现)
                .setUseScriptCache(true)
                .setCodec(codec)
            val singleServerConfig = redissonProperties.singleServerConfig
            if (ObjectUtil.isNotNull(singleServerConfig)) {
                // 使用单机模式
                config.useSingleServer() //设置redis key前缀
                    .setNameMapper(KeyPrefixHandler(redissonProperties.keyPrefix))
                    .setTimeout(singleServerConfig!!.timeout)
                    .setClientName(singleServerConfig.clientName)
                    .setIdleConnectionTimeout(singleServerConfig.idleConnectionTimeout)
                    .setSubscriptionConnectionPoolSize(singleServerConfig.subscriptionConnectionPoolSize)
                    .setConnectionMinimumIdleSize(singleServerConfig.connectionMinimumIdleSize)
                    .setConnectionPoolSize(singleServerConfig.connectionPoolSize)
            }
            // 集群配置方式 参考下方注释
            val clusterServersConfig = redissonProperties.clusterServersConfig
            if (ObjectUtil.isNotNull(clusterServersConfig)) {
                config.useClusterServers() //设置redis key前缀
                    .setNameMapper(KeyPrefixHandler(redissonProperties.keyPrefix))
                    .setTimeout(clusterServersConfig!!.timeout)
                    .setClientName(clusterServersConfig.clientName)
                    .setIdleConnectionTimeout(clusterServersConfig.idleConnectionTimeout)
                    .setSubscriptionConnectionPoolSize(clusterServersConfig.subscriptionConnectionPoolSize)
                    .setMasterConnectionMinimumIdleSize(clusterServersConfig.masterConnectionMinimumIdleSize)
                    .setMasterConnectionPoolSize(clusterServersConfig.masterConnectionPoolSize)
                    .setSlaveConnectionMinimumIdleSize(clusterServersConfig.slaveConnectionMinimumIdleSize)
                    .setSlaveConnectionPoolSize(clusterServersConfig.slaveConnectionPoolSize)
                    .setReadMode(clusterServersConfig.readMode)
                    .setSubscriptionMode(clusterServersConfig.subscriptionMode)
            }
            log.info("初始化 redis 配置")
        }
    }

    /**
     * 自定义缓存管理器 整合spring-cache
     */
    @Bean
    fun cacheManager(): CacheManager {
        return PlusSpringCacheManager()
    }
    /**
     * redis集群配置 yml
     *
     * --- # redis 集群配置(单机与集群只能开启一个另一个需要注释掉)
     * spring.data:
     * redis:
     * cluster:
     * nodes:
     * - 192.168.0.100:6379
     * - 192.168.0.101:6379
     * - 192.168.0.102:6379
     * # 密码
     * password:
     * # 连接超时时间
     * timeout: 10s
     * # 是否开启ssl
     * ssl.enabled: false
     *
     * redisson:
     * # 线程池数量
     * threads: 16
     * # Netty线程池数量
     * nettyThreads: 32
     * # 集群配置
     * clusterServersConfig:
     * # 客户端名称
     * clientName: ${ruoyi.name}
     * # master最小空闲连接数
     * masterConnectionMinimumIdleSize: 32
     * # master连接池大小
     * masterConnectionPoolSize: 64
     * # slave最小空闲连接数
     * slaveConnectionMinimumIdleSize: 32
     * # slave连接池大小
     * slaveConnectionPoolSize: 64
     * # 连接空闲超时，单位：毫秒
     * idleConnectionTimeout: 10000
     * # 命令等待超时，单位：毫秒
     * timeout: 3000
     * # 发布和订阅连接池大小
     * subscriptionConnectionPoolSize: 50
     * # 读取模式
     * readMode: "SLAVE"
     * # 订阅模式
     * subscriptionMode: "MASTER"
     */
}
