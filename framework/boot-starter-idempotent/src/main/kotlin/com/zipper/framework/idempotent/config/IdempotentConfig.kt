package com.zipper.framework.idempotent.config

import com.zipper.framework.idempotent.aspectj.RepeatSubmitAspect
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConfiguration

/**
 * 幂等功能配置
 *
 * @author Lion Li
 */
@AutoConfiguration(after = [RedisConfiguration::class])
class IdempotentConfig {
    @Bean
    fun repeatSubmitAspect(): RepeatSubmitAspect {
        return RepeatSubmitAspect()
    }
}
