package com.zipper.server.web.controller

import cn.dev33.satoken.annotation.SaIgnore
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ObjectUtil
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.domain.R
import com.zipper.framework.core.domain.model.LoginBody
import com.zipper.framework.core.domain.model.RegisterBody
import com.zipper.framework.core.domain.model.SocialLoginBody
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.MapstructUtils
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.framework.core.utils.MessageUtils.message
import com.zipper.framework.core.utils.StreamUtils
import com.zipper.framework.core.utils.ValidatorUtils.validate
import com.zipper.framework.encrypt.annotation.ApiEncrypt
import com.zipper.framework.json.utils.JsonUtils.parseObject
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.social.config.properties.SocialProperties
import com.zipper.framework.social.utils.SocialUtils.getAuthRequest
import com.zipper.framework.social.utils.SocialUtils.loginAuth
import com.zipper.framework.tanent.helper.TenantHelper
import com.zipper.framework.websocket.utils.WebSocketUtils.sendMessage
import com.zipper.modules.system.domain.bo.SysTenantBo
import com.zipper.modules.system.service.client.ISysClientService
import com.zipper.modules.system.service.config.ISysConfigService
import com.zipper.modules.system.service.social.ISysSocialService
import com.zipper.modules.system.service.tenant.ISysTenantService
import com.zipper.server.web.domain.vo.LoginTenantVo
import com.zipper.server.web.domain.vo.LoginVo
import com.zipper.server.web.domain.vo.TenantListVo
import com.zipper.server.web.service.IAuthStrategy
import com.zipper.server.web.service.SysLoginService
import com.zipper.server.web.service.SysRegisterService
import jakarta.servlet.http.HttpServletRequest
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.utils.AuthStateUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URL
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 认证
 *
 * @author Lion Li
 */
@SaIgnore
@RestController
@RequestMapping("/auth")
class AuthController(
    private val socialProperties: SocialProperties,
    private val loginService: SysLoginService,
    private val registerService: SysRegisterService,
    private val configService: ISysConfigService,
    private val tenantService: ISysTenantService,
    private val socialUserService: ISysSocialService,
    private val clientService: ISysClientService,
    private val scheduledExecutorService: ScheduledExecutorService
) {
    /**
     * 登录方法
     *
     * @param body 登录信息
     * @return 结果
     */
    @ApiEncrypt
    @PostMapping("/login")
    fun login(@RequestBody body: String?): R<LoginVo> {
        val loginBody = parseObject(body, LoginBody::class.java)
        validate(loginBody)
        // 授权类型和客户端id
        val clientId = loginBody.clientId
        val grantType = loginBody.grantType
        val client = clientService.queryByClientId(clientId)
        // 查询不到 client 或 client 内不包含 grantType
        if (ObjectUtil.isNull(client) || !StringUtils.contains(client!!.grantType, grantType)) {
            log.info("客户端id: {} 认证类型：{} 异常!.", clientId, grantType)
            return R.fail(message("auth.grant.type.error"))
        } else if (UserConstants.NORMAL != client.status) {
            return R.fail(message("auth.grant.type.blocked"))
        }
        // 校验租户
        loginService.checkTenant(loginBody.tenantId)
        // 登录
        val loginVo: LoginVo = IAuthStrategy.login(body!!, client, grantType)

        val userId: Long = LoginHelper.getUserId()
        scheduledExecutorService.schedule({
            sendMessage(userId, "欢迎登录RuoYi-Vue-Plus后台管理系统")
        }, 3, TimeUnit.SECONDS)
        return R.ok(loginVo)
    }

    /**
     * 第三方登录请求
     *
     * @param source 登录来源
     * @return 结果
     */
    @GetMapping("/binding/{source}")
    fun authBinding(@PathVariable("source") source: String): R<String> {
        val obj = socialProperties.type[source]
        if (ObjectUtil.isNull(obj)) {
            return R.fail(source + "平台账号暂不支持")
        }
        val authRequest = getAuthRequest(source, socialProperties)
        val authorizeUrl = authRequest.authorize(AuthStateUtils.createState())
        return R.ok("操作成功", authorizeUrl)
    }

    /**
     * 第三方登录回调业务处理 绑定授权
     *
     * @param loginBody 请求体
     * @return 结果
     */
    @PostMapping("/social/callback")
    fun socialCallback(@RequestBody loginBody: SocialLoginBody): R<Void> {
        // 获取第三方登录信息
        val response: AuthResponse<AuthUser> = loginAuth(
            loginBody.source, loginBody.socialCode,
            loginBody.socialState, socialProperties
        )
        val authUserData: AuthUser = response.getData()
        // 判断授权响应是否成功
        if (!response.ok()) {
            return R.fail(response.getMsg())
        }
        loginService.socialRegister(authUserData)
        return R.ok()
    }


    /**
     * 取消授权
     *
     * @param socialId socialId
     */
    @DeleteMapping(value = ["/unlock/{socialId}"])
    fun unlockSocial(@PathVariable socialId: Long?): R<Void> {
        val rows = socialUserService.deleteWithValidById(socialId)
        return if (rows) R.ok() else R.fail("取消授权失败")
    }


    /**
     * 退出登录
     */
    @PostMapping("/logout")
    fun logout(): R<Void> {
        loginService.logout()
        return R.ok("退出成功")
    }

    /**
     * 用户注册
     */
    @ApiEncrypt
    @PostMapping("/register")
    fun register(@Validated @RequestBody user: RegisterBody): R<Void> {
        if (!configService.selectRegisterEnabled(user.tenantId)) {
            return R.fail("当前系统没有开启注册功能！")
        }
        registerService.register(user)
        return R.ok()
    }

    /**
     * 登录页面租户下拉框
     *
     * @return 租户列表
     */
    @GetMapping("/tenant/list")
    @Throws(Exception::class)
    fun tenantList(request: HttpServletRequest): R<LoginTenantVo> {
        val tenantList = tenantService.queryList(SysTenantBo())
        val voList: List<TenantListVo> = MapstructUtils.convertWithClass(tenantList, TenantListVo::class.java)
        // 获取域名
        val host: String
        val referer = request.getHeader("referer")
        host = if (StringUtils.isNotBlank(referer)) {
            // 这里从referer中取值是为了本地使用hosts添加虚拟域名，方便本地环境调试
            referer.split("//")[1].split("/").toString()
        } else {
            URL(request.requestURL.toString()).host
        }
        // 根据域名进行筛选

        val list = voList.filter { it.domain == host }
        // 返回对象
        val vo = LoginTenantVo()
        vo.voList = if (CollUtil.isNotEmpty(list)) list else voList
        vo.tenantEnabled = TenantHelper.isEnable()
        return R.ok(vo)
    }
}
