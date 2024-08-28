package com.zipper.server.web.listener

import cn.dev33.satoken.config.SaTokenConfig
import cn.dev33.satoken.listener.SaTokenListener
import cn.dev33.satoken.stp.SaLoginModel
import cn.hutool.http.useragent.UserAgentUtil
import com.zipper.framework.core.constant.CacheConstants
import com.zipper.framework.core.constant.Constants
import com.zipper.framework.core.domain.dto.UserOnlineDTO
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.MessageUtils
import com.zipper.framework.core.utils.ServletUtils
import com.zipper.framework.core.utils.ServletUtils.getRequest
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.framework.core.utils.ip.AddressUtils
import com.zipper.framework.log.event.LoginLogEvent
import com.zipper.framework.redis.utils.RedisUtils
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.server.web.helper.LoginLogEventHelper
import com.zipper.server.web.service.SysLoginService
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * 用户行为 侦听器的实现
 *
 * @author Lion Li
 */
@Component
class UserActionListener(
    private val tokenConfig: SaTokenConfig,
    private val loginService: SysLoginService
) : SaTokenListener {
    /**
     * 每次登录时触发
     */
    override fun doLogin(loginType: String, loginId: Any, tokenValue: String, loginModel: SaLoginModel) {
        val userAgent = UserAgentUtil.parse(getRequest()!!.getHeader("User-Agent"))
        val ip: String = ServletUtils.getClientIP()
        val user = LoginHelper.getLoginUser()
        val dto = UserOnlineDTO()
        dto.ipaddr = ip
        dto.loginLocation = AddressUtils.getRealAddressByIP(ip)
        dto.browser = userAgent.browser.name
        dto.os = userAgent.os.name
        dto.loginTime = System.currentTimeMillis()
        dto.tokenId = tokenValue
        dto.userName = user?.username
        dto.clientKey = user?.clientKey
        dto.deviceType = user?.deviceType
        dto.deptName = user?.deptName
        if (tokenConfig.timeout.toInt() == -1) {
            RedisUtils.setCacheObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto)
        } else {
            RedisUtils.setCacheObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto, Duration.ofSeconds(tokenConfig.getTimeout()))
        }
        // 记录登录日志
        LoginLogEventHelper.postRecord(
            tenantId = user?.tenantId,
            username = user?.username,
            status = Constants.LOGIN_SUCCESS,
            message = MessageUtils.message("user.login.success"),
        )

        // 更新登录信息
        loginService.recordLoginInfo(user?.userId, ip)
        log.info("user doLogin, userId:{}, token:{}", loginId, tokenValue)
    }

    /**
     * 每次注销时触发
     */
    override fun doLogout(loginType: String, loginId: Any, tokenValue: String) {
        RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue)
        log.info("user doLogout, userId:{}, token:{}", loginId, tokenValue)
    }

    /**
     * 每次被踢下线时触发
     */
    override fun doKickout(loginType: String, loginId: Any, tokenValue: String) {
        RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue)
        log.info("user doKickout, userId:{}, token:{}", loginId, tokenValue)
    }

    /**
     * 每次被顶下线时触发
     */
    override fun doReplaced(loginType: String, loginId: Any, tokenValue: String) {
        RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue)
        log.info("user doReplaced, userId:{}, token:{}", loginId, tokenValue)
    }

    /**
     * 每次被封禁时触发
     */
    override fun doDisable(loginType: String, loginId: Any, service: String, level: Int, disableTime: Long) {
    }

    /**
     * 每次被解封时触发
     */
    override fun doUntieDisable(loginType: String, loginId: Any, service: String) {
    }

    /**
     * 每次打开二级认证时触发
     */
    override fun doOpenSafe(loginType: String, tokenValue: String, service: String, safeTime: Long) {
    }

    /**
     * 每次创建Session时触发
     */
    override fun doCloseSafe(loginType: String, tokenValue: String, service: String) {
    }

    /**
     * 每次创建Session时触发
     */
    override fun doCreateSession(id: String) {
    }

    /**
     * 每次注销Session时触发
     */
    override fun doLogoutSession(id: String) {
    }

    /**
     * 每次Token续期时触发
     */
    override fun doRenewTimeout(tokenValue: String, loginId: Any, timeout: Long) {
    }
}
