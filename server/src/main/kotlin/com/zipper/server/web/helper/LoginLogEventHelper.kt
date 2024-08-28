package com.zipper.server.web.helper

import cn.hutool.extra.servlet.JakartaServletUtil
import cn.hutool.http.useragent.UserAgentUtil
import com.zipper.framework.core.utils.ServletUtils
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.framework.log.event.LoginLogEvent
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.modules.auth.service.ISysClientService
import org.apache.commons.lang3.StringUtils

object LoginLogEventHelper {
    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    fun postRecord(tenantId: String?, username: String?, status: String?, message: String?) {
        val request = ServletUtils.getRequest() ?: return
        val clientId = request.getHeader(LoginHelper.CLIENT_KEY)
        var clientKey: String? = null
        var deviceType: String? = null

        if (StringUtils.isNotBlank(clientId)) {
            val clientService = SpringUtilExt.getBeanByType(ISysClientService::class.java)
            val client = clientService.queryByClientId(clientId)
            clientKey = client?.clientKey
            deviceType = client?.deviceType
        }

        val userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"))
        val ip = JakartaServletUtil.getClientIP(request)

        SpringUtilExt.context().publishEvent(
            LoginLogEvent(
                tenantId = tenantId,
                username = username,
                status = status,
                message = message,
                userAgent = userAgent,
                ip = ip,
                clientKey = clientKey,
                deviceType = deviceType
            )
        )
    }
}