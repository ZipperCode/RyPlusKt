package com.zipper.framework.tanent.config.properties

import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 租户 配置属性
 *
 * @author Lion Li
 */
@ConfigurationProperties(prefix = "tenant")
class TenantProperties {
    /**
     * 是否启用
     */
    var enable: Boolean = false

    /**
     * 排除表
     */
    var excludes: List<String> = emptyList()
}
