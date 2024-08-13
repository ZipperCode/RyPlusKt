package com.zipper.framework.websocket.config.properties

import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * WebSocket 配置项
 *
 * @author zendwang
 */
@Data
@ConfigurationProperties("websocket")
class WebSocketProperties {
    var enabled: Boolean = false

    /**
     * 路径
     */
    var path: String = ""

    /**
     * 设置访问源地址
     */
    var allowedOrigins: String? = null
}
