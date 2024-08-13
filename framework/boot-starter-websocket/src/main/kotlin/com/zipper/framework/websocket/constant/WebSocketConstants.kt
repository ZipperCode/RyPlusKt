package com.zipper.framework.websocket.constant

/**
 * websocket的常量配置
 *
 * @author zendwang
 */
object WebSocketConstants {

    /**
     * websocketSession中的参数的key
     */
    const val LOGIN_USER_KEY: String = "loginUser"

    /**
     * 订阅的频道
     */
    const val WEB_SOCKET_TOPIC: String = "global:websocket"

    /**
     * 前端心跳检查的命令
     */
    const val PING: String = "ping"

    /**
     * 服务端心跳恢复的字符串
     */
    const val PONG: String = "pong"
}

