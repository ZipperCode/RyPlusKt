package com.zipper.framework.websocket.interceptor

import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.websocket.constant.WebSocketConstants.LOGIN_USER_KEY
import lombok.extern.slf4j.Slf4j
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

/**
 * WebSocket握手请求的拦截器
 *
 * @author zendwang
 */
@Slf4j
class PlusWebSocketInterceptor : HandshakeInterceptor {
    /**
     * 握手前
     *
     * @param request    request
     * @param response   response
     * @param wsHandler  wsHandler
     * @param attributes attributes
     * @return 是否握手成功
     */
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val loginUser = LoginHelper.getLoginUser() ?: return false
        attributes[LOGIN_USER_KEY] = loginUser
        return true
    }

    /**
     * 握手后
     *
     * @param request   request
     * @param response  response
     * @param wsHandler wsHandler
     * @param exception 异常
     */
    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) = Unit
}
