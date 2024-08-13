package com.zipper.framework.websocket.dto

import java.io.Serial
import java.io.Serializable

/**
 * 消息的dto
 *
 * @author zendwang
 */
class WebSocketMessageDto : Serializable {
    /**
     * 需要推送到的session key 列表
     */
    var sessionKeys: List<Long> = emptyList()

    /**
     * 需要发送的消息
     */
    var message: String = ""

    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
