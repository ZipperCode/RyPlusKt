package com.zipper.framework.log.event

import jakarta.servlet.http.HttpServletRequest
import java.io.Serial
import java.io.Serializable

/**
 * 登录事件
 *
 * @author Lion Li
 */
class LoginLogEvent constructor(
    /**
     * 租户ID
     */
    val tenantId: String? = null,

    /**
     * 用户账号
     */
    val username: String? = null,

    /**
     * 登录状态 0成功 1失败
     */
    val status: String? = null,
    /**
     * 提示消息
     */
    val message: String? = null,

    /**
     * 请求体
     */
    val request: HttpServletRequest? = null,

    /**
     * 其他参数
     */
    val args: Array<Any> = emptyArray()
) : Serializable {

    companion object {
        @Serial
        private val serialVersionUID = 1L
    }
}

