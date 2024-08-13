package com.zipper.framework.websocket.handler

import com.zipper.framework.core.domain.model.LoginUser
import com.zipper.framework.websocket.constant.WebSocketConstants
import com.zipper.framework.websocket.dto.WebSocketMessageDto
import com.zipper.framework.websocket.holder.WebSocketSessionHolder
import com.zipper.framework.websocket.utils.WebSocketUtils
import lombok.extern.slf4j.Slf4j
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.AbstractWebSocketHandler
import com.zipper.framework.core.ext.log

/**
 * WebSocketHandler 实现类
 *
 * @author zendwang
 */
@Slf4j
class PlusWebSocketHandler : AbstractWebSocketHandler() {
    /**
     * 连接成功后
     */
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val loginUser = session.attributes[WebSocketConstants.LOGIN_USER_KEY] as LoginUser?
        WebSocketSessionHolder.addSession(loginUser!!.userId, session)
        log.info("[connect] sessionId: {},userId:{},userType:{}", session.id, loginUser.userId, loginUser.userType)
    }

    /**
     * 处理发送来的文本消息
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val loginUser = session.attributes[WebSocketConstants.LOGIN_USER_KEY] as LoginUser?
        val userIds = listOf(loginUser!!.userId)
        val webSocketMessageDto = WebSocketMessageDto()
        webSocketMessageDto.sessionKeys = userIds
        webSocketMessageDto.message = message.payload
        WebSocketUtils.publishMessage(webSocketMessageDto)
    }

    @Throws(Exception::class)
    override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
        super.handleBinaryMessage(session, message)
    }

    /**
     * 心跳监测的回复
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun handlePongMessage(session: WebSocketSession, message: PongMessage) {
        WebSocketUtils.sendPongMessage(session)
    }

    /**
     * 连接出错时
     *
     * @param session
     * @param exception
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        log.error("[transport error] sessionId: {} , exception:{}", session.id, exception.message)
    }

    /**
     * 连接关闭后
     *
     * @param session
     * @param status
     */
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val loginUser = session.attributes[WebSocketConstants.LOGIN_USER_KEY] as LoginUser?
        WebSocketSessionHolder.removeSession(loginUser!!.userId)
        log.info("[disconnect] sessionId: {},userId:{},userType:{}", session.id, loginUser.userId, loginUser.userType)
    }

    /**
     * 是否支持分片消息
     *
     * @return
     */
    override fun supportsPartialMessages(): Boolean {
        return false
    }
}
