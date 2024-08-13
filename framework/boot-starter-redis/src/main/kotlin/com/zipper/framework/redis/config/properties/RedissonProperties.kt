package com.zipper.framework.redis.config.properties

import org.redisson.config.ReadMode
import org.redisson.config.SubscriptionMode
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Redisson 配置属性
 *
 * @author Lion Li
 */
@ConfigurationProperties(prefix = "redisson")
class RedissonProperties {
    /**
     * redis缓存key前缀
     */
    var keyPrefix: String = ""

    /**
     * 线程池数量,默认值 = 当前处理核数量 * 2
     */
    var threads = 0

    /**
     * Netty线程池数量,默认值 = 当前处理核数量 * 2
     */
    var nettyThreads = 0

    /**
     * 单机服务配置
     */
    var singleServerConfig: SingleServerConfig? = null

    /**
     * 集群服务配置
     */
    var clusterServersConfig: ClusterServersConfig? = null

    class SingleServerConfig {
        /**
         * 客户端名称
         */
        var clientName: String = ""

        /**
         * 最小空闲连接数
         */
        var connectionMinimumIdleSize = 0

        /**
         * 连接池大小
         */
        var connectionPoolSize = 0

        /**
         * 连接空闲超时，单位：毫秒
         */
        var idleConnectionTimeout = 0

        /**
         * 命令等待超时，单位：毫秒
         */
        var timeout = 0

        /**
         * 发布和订阅连接池大小
         */
        var subscriptionConnectionPoolSize = 0
    }


    class ClusterServersConfig {
        /**
         * 客户端名称
         */
        var clientName: String = ""

        /**
         * master最小空闲连接数
         */
        var masterConnectionMinimumIdleSize = 0

        /**
         * master连接池大小
         */
        var masterConnectionPoolSize = 0

        /**
         * slave最小空闲连接数
         */
        var slaveConnectionMinimumIdleSize = 0

        /**
         * slave连接池大小
         */
        var slaveConnectionPoolSize = 0

        /**
         * 连接空闲超时，单位：毫秒
         */
        var idleConnectionTimeout = 0

        /**
         * 命令等待超时，单位：毫秒
         */
        var timeout = 0

        /**
         * 发布和订阅连接池大小
         */
        var subscriptionConnectionPoolSize = 0

        /**
         * 读取模式
         */
        var readMode: ReadMode = ReadMode.MASTER

        /**
         * 订阅模式
         */
        var subscriptionMode: SubscriptionMode = SubscriptionMode.MASTER
    }
}
