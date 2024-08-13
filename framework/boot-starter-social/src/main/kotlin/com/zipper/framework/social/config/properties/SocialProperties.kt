package com.zipper.framework.social.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Social 配置属性
 *
 * @author thiszhc
 */
@ConfigurationProperties(prefix = "justauth")
class SocialProperties {
    /**
     * 是否启用
     */
    var enabled: Boolean = false

    /**
     * 授权类型
     */
    var type: Map<String, SocialLoginConfigProperties> = emptyMap()
}
