package com.zipper.server.web.service.impl

import cn.dev33.satoken.secure.BCrypt
import cn.dev33.satoken.stp.SaLoginModel
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.ObjectUtil
import com.zipper.framework.core.constant.Constants
import com.zipper.framework.core.constant.GlobalConstants
import com.zipper.framework.core.domain.model.PasswordLoginBody
import com.zipper.framework.core.enums.LoginType
import com.zipper.framework.core.enums.UserStatus
import com.zipper.framework.core.exception.user.CaptchaException
import com.zipper.framework.core.exception.user.CaptchaExpireException
import com.zipper.framework.core.exception.user.UserException
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.MessageUtils.message
import com.zipper.framework.core.utils.ValidatorUtils.validate
import com.zipper.framework.json.utils.JsonUtils.parseObject
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.redis.utils.RedisUtils.deleteObject
import com.zipper.framework.redis.utils.RedisUtils.getCacheObject
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.satoken.utils.LoginHelper.login
import com.zipper.framework.tanent.helper.TenantHelper
import com.zipper.framework.web.config.properties.CaptchaProperties
import com.zipper.modules.auth.domain.entity.SysClientEntity
import com.zipper.modules.system.domain.entity.SysUserEntity
import com.zipper.modules.system.domain.vo.SysUserVo
import com.zipper.modules.system.mapper.SysUserMapper
import com.zipper.server.web.domain.vo.LoginVo
import com.zipper.server.web.service.IAuthStrategy
import com.zipper.server.web.service.SysLoginService
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service

/**
 * 密码认证策略
 *
 * @author Michelle.Chung
 */
@Service("password" + IAuthStrategy.BASE_NAME)
class PasswordAuthStrategy(
    private val captchaProperties: CaptchaProperties,
    private val loginService: SysLoginService,
    private val userMapper: SysUserMapper
) : IAuthStrategy {
    override fun login(body: String, client: SysClientEntity): LoginVo {
        val loginBody = parseObject(body, PasswordLoginBody::class.java)
        validate(loginBody)
        val tenantId = loginBody.tenantId ?:""
        val username = loginBody.username ?: ""
        val password = loginBody.password ?:""
        val code = loginBody.code ?: ""
        val uuid = loginBody.uuid ?: ""

        val captchaEnabled = captchaProperties.enable
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(tenantId, username, code, uuid)
        }

        val user = loadUserByUsername(tenantId, username)
        loginService.checkLogin(LoginType.PASSWORD, tenantId, username) { !BCrypt.checkpw(password, user.password) }
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        val loginUser = loginService.buildLoginUser(user)
        loginUser.clientKey = client.clientKey
        loginUser.deviceType = client.deviceType
        val model = SaLoginModel()
        model.setDevice(client.deviceType)
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.timeout!!)
        model.setActiveTimeout(client.activeTimeout!!)
        model.setExtra(LoginHelper.CLIENT_KEY, client.clientId)
        // 生成token
        login(loginUser, model)

        val loginVo = LoginVo()
        loginVo.accessToken = StpUtil.getTokenValue()
        loginVo.expireIn = StpUtil.getTokenTimeout()
        loginVo.clientId = client.clientId
        return loginVo
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    private fun validateCaptcha(tenantId: String, username: String, code: String, uuid: String) {
        val verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.defaultString(uuid, "")
        val captcha = getCacheObject<String>(verifyKey)
        deleteObject(verifyKey)
        if (captcha == null) {
            loginService.recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, message("user.jcaptcha.expire"))
            throw CaptchaExpireException()
        }
        if (!code.equals(captcha, ignoreCase = true)) {
            loginService.recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, message("user.jcaptcha.error"))
            throw CaptchaException()
        }
    }

    private fun loadUserByUsername(tenantId: String, username: String): SysUserVo {
        return TenantHelper.dynamic(tenantId) {
            val user = userMapper.selectOne(
                MybatisKt.ktQuery<SysUserEntity>()
                    .select(SysUserEntity::userName, SysUserEntity::status)
                    .eq(SysUserEntity::userName, username)
            )
            if (ObjectUtil.isNull(user)) {
                log.info("登录用户：{} 不存在.", username)
                throw UserException("user.not.exists", username)
            } else if (UserStatus.DISABLE.code == user.status) {
                log.info("登录用户：{} 已被停用.", username)
                throw UserException("user.blocked", username)
            }
            userMapper.selectUserByUserName(username)!!
        }
    }
}
