package com.zipper.framework.websocket.utils

import cn.hutool.core.collection.CollUtil
import com.zipper.framework.redis.utils.RedisUtils
import com.zipper.framework.websocket.constant.WebSocketConstants
import com.zipper.framework.websocket.dto.WebSocketMessageDto
import com.zipper.framework.websocket.holder.WebSocketSessionHolder
import org.springframework.web.socket.PongMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import com.zipper.framework.core.ext.log
import java.io.IOException
import java.util.function.Consumer

/**
 * 工具类
 *
 * @author zendwang
 */
object WebSocketUtils {
    /**
     * 发送消息
     *
     * @param sessionKey session主键 一般为用户id
     * @param message    消息文本
     */
    @JvmStatic
    fun sendMessage(sessionKey: Long, message: String) {
        val session = WebSocketSessionHolder.getSessions(sessionKey)
        sendMessage(session, message)
    }

    /**
     * 订阅消息
     *
     * @param consumer 自定义处理
     */
    @JvmStatic
    fun subscribeMessage(consumer: Consumer<WebSocketMessageDto>) {
        RedisUtils.subscribe(WebSocketConstants.WEB_SOCKET_TOPIC, WebSocketMessageDto::class.java, consumer)
    }

    /**
     * 发布订阅的消息
     *
     * @param webSocketMessage 消息对象
     */
    @JvmStatic
    fun publishMessage(webSocketMessage: WebSocketMessageDto) {
        val unsentSessionKeys = ArrayList<Long>()
        // 当前服务内session,直接发送消息
        for (sessionKey in webSocketMessage.sessionKeys) {
            if (WebSocketSessionHolder.existSession(sessionKey)) {
                sendMessage(sessionKey, webSocketMessage.message)
                continue
            }
            unsentSessionKeys.add(sessionKey)
        }
        // 不在当前服务内session,发布订阅消息
        if (CollUtil.isNotEmpty(unsentSessionKeys)) {
            val broadcastMessage = WebSocketMessageDto()
            broadcastMessage.message = webSocketMessage.message
            broadcastMessage.sessionKeys = unsentSessionKeys
            RedisUtils.publish(WebSocketConstants.WEB_SOCKET_TOPIC, broadcastMessage) {
                log.info(
                    " WebSocket发送主题订阅消息topic:{} session keys:{} message:{}",
                    WebSocketConstants.WEB_SOCKET_TOPIC, unsentSessionKeys, webSocketMessage.message
                )
            }
        }
    }

    /**
     * 发布订阅的消息(群发)
     *
     * @param message 消息内容
     */
    @JvmStatic
    fun publishAll(message: String?) {
        val broadcastMessage = WebSocketMessageDto()
        broadcastMessage.message = message!!
        RedisUtils.publish(WebSocketConstants.WEB_SOCKET_TOPIC, broadcastMessage) {
            log.info("WebSocket发送主题订阅消息topic:{} message:{}", WebSocketConstants.WEB_SOCKET_TOPIC, message)
        }
    }

    @JvmStatic
    fun sendPongMessage(session: WebSocketSession?) {
        sendMessage(session, PongMessage())
    }

    @JvmStatic
    fun sendMessage(session: WebSocketSession?, message: String) {
        sendMessage(session, TextMessage(message))
    }

    @JvmStatic
    private fun sendMessage(session: WebSocketSession?, message: WebSocketMessage<*>) {
        if (session == null || !session.isOpen) {
            log.warn("[send] session会话已经关闭")
        } else {
            try {
                session.sendMessage(message)
            } catch (e: IOException) {
                log.error("[send] session({}) 发送消息({}) 异常", session, message, e)
            }
        }
    }
}
