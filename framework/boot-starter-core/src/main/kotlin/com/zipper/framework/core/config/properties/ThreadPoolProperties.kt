package com.zipper.framework.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @param enabled 是否开启线程池
 * @param queueCapacity 队列最大长度
 * @param keepAliveSeconds 线程池维护线程所允许的空闲时间
 */
@ConfigurationProperties(prefix = "thread-pool")
class ThreadPoolProperties(
    val enabled: Boolean = false,
    val queueCapacity: Int = 256,
    val keepAliveSeconds: Int = 60
)