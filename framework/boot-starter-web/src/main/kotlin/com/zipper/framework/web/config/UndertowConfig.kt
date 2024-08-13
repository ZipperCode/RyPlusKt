package com.zipper.framework.web.config

import io.undertow.server.DefaultByteBufferPool
import io.undertow.servlet.api.DeploymentInfo
import io.undertow.websockets.jsr.WebSocketDeploymentInfo
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer

/**
 * Undertow 自定义配置
 *
 * @author Lion Li
 */
@AutoConfiguration
class UndertowConfig : WebServerFactoryCustomizer<UndertowServletWebServerFactory> {
    /**
     * 设置 Undertow 的 websocket 缓冲池
     */
    override fun customize(factory: UndertowServletWebServerFactory) {
        // 默认不直接分配内存 如果项目中使用了 websocket 建议直接分配
        factory.addDeploymentInfoCustomizers(UndertowDeploymentInfoCustomizer { deploymentInfo: DeploymentInfo ->
            val webSocketDeploymentInfo = WebSocketDeploymentInfo()
            webSocketDeploymentInfo.setBuffers(DefaultByteBufferPool(false, 512))
            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo)
        })
    }
}
