package com.zipper.framework.social.config

import com.zipper.framework.social.config.properties.SocialProperties
import com.zipper.framework.social.utils.AuthRedisStateCache
import me.zhyd.oauth.cache.AuthStateCache
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * Social 配置属性
 * @author thiszhc
 */
@AutoConfiguration
@EnableConfigurationProperties(SocialProperties::class)
class SocialAutoConfiguration {
    @Bean
    fun authStateCache(): AuthStateCache {
        return AuthRedisStateCache()
    }
}
