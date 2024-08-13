package com.zipper.server.web.service.impl

import cn.dev33.satoken.stp.SaLoginModel
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.ObjectUtil
import com.zipper.framework.core.constant.Constants
import com.zipper.framework.core.constant.GlobalConstants
import com.zipper.framework.core.domain.model.SmsLoginBody
import com.zipper.framework.core.enums.LoginType
import com.zipper.framework.core.enums.UserStatus
import com.zipper.framework.core.exception.user.CaptchaExpireException
import com.zipper.framework.core.exception.user.UserException
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.MessageUtils.message
import com.zipper.framework.core.utils.ValidatorUtils.validate
import com.zipper.framework.json.utils.JsonUtils.parseObject
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.redis.utils.RedisUtils.getCacheObject
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.satoken.utils.LoginHelper.login
import com.zipper.framework.tanent.helper.TenantHelper
import com.zipper.modules.system.domain.entity.SysClientEntity
import com.zipper.modules.system.domain.entity.SysUserEntity
import com.zipper.modules.system.domain.vo.SysUserVo
import com.zipper.modules.system.mapper.SysUserMapper
import com.zipper.server.web.domain.vo.LoginVo
import com.zipper.server.web.service.IAuthStrategy
import com.zipper.server.web.service.SysLoginService
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service

/**
 * 短信认证策略
 *
 * @author Michelle.Chung
 */
@Service("sms" + IAuthStrategy.BASE_NAME)
class SmsAuthStrategy(
    private val loginService: SysLoginService,
    private val userMapper: SysUserMapper
) : IAuthStrategy {
    override fun login(body: String, client: SysClientEntity): LoginVo {
        val loginBody = parseObject(body, SmsLoginBody::class.java)!!
        validate(loginBody)
        val tenantId = loginBody.tenantId ?: ""
        val phonenumber = loginBody.phonenumber ?: ""
        val smsCode = loginBody.smsCode ?: ""

        // 通过手机号查找用户
        val user = loadUserByPhonenumber(tenantId, phonenumber)

        loginService.checkLogin(LoginType.SMS, tenantId, user.userName) { !validateSmsCode(tenantId, phonenumber, smsCode) }
        // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
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
     * 校验短信验证码
     */
    private fun validateSmsCode(tenantId: String, phonenumber: String, smsCode: String): Boolean {
        val code = getCacheObject<String>(GlobalConstants.CAPTCHA_CODE_KEY + phonenumber)
        if (StringUtils.isBlank(code)) {
            loginService.recordLogininfor(tenantId, phonenumber, Constants.LOGIN_FAIL, message("user.jcaptcha.expire"))
            throw CaptchaExpireException()
        }
        return code == smsCode
    }

    private fun loadUserByPhonenumber(tenantId: String, phonenumber: String): SysUserVo {
        return TenantHelper.dynamic(tenantId) {
            val user = userMapper.selectOne(
                MybatisKt.ktQuery<SysUserEntity>()
                    .select(SysUserEntity::phonenumber, SysUserEntity::status)
                    .eq(SysUserEntity::phonenumber, phonenumber)
            )
            if (ObjectUtil.isNull(user)) {
                log.info("登录用户：{} 不存在.", phonenumber)
                throw UserException("user.not.exists", phonenumber)
            } else if (UserStatus.DISABLE.code == user.status) {
                log.info("登录用户：{} 已被停用.", phonenumber)
                throw UserException("user.blocked", phonenumber)
            }
            userMapper.selectUserByPhonenumber(phonenumber)!!
        }
    }
}
