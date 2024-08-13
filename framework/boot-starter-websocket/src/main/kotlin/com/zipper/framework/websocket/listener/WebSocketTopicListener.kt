package com.zipper.framework.websocket.listener

import cn.hutool.core.collection.CollUtil
import com.zipper.framework.websocket.holder.WebSocketSessionHolder
import com.zipper.framework.websocket.utils.WebSocketUtils
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.Ordered
import com.zipper.framework.core.ext.log

/**
 * WebSocket 主题订阅监听器
 *
 * @author zendwang
 */
class WebSocketTopicListener : ApplicationRunner, Ordered {
    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        WebSocketUtils.subscribeMessage { message ->
            log.info(
                "WebSocket主题订阅收到消息session keys={} message={}",
                message.sessionKeys,
                message.message
            )
            // 如果key不为空就按照key发消息 如果为空就群发
            if (CollUtil.isNotEmpty(message.sessionKeys)) {
                message.sessionKeys.forEach { key ->
                    if (WebSocketSessionHolder.existSession(key)) {
                        WebSocketUtils.sendMessage(key, message.message)
                    }
                }
            } else {
                WebSocketSessionHolder.getSessionsAll().forEach { key ->
                    WebSocketUtils.sendMessage(key, message.message)
                }
            }
        }
        log.info("初始化WebSocket主题订阅监听器成功")
    }

    override fun getOrder(): Int {
        return -1
    }
}
