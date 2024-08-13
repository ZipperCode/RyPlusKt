package com.zipper.server.web.service

import cn.dev33.satoken.secure.BCrypt
import cn.hutool.core.util.ObjectUtil
import com.zipper.framework.core.constant.Constants
import com.zipper.framework.core.constant.GlobalConstants
import com.zipper.framework.core.domain.model.RegisterBody
import com.zipper.framework.core.enums.UserType
import com.zipper.framework.core.exception.user.CaptchaException
import com.zipper.framework.core.exception.user.CaptchaExpireException
import com.zipper.framework.core.exception.user.UserException
import com.zipper.framework.core.utils.MessageUtils.message
import com.zipper.framework.core.utils.ServletUtils
import com.zipper.framework.core.utils.SpringUtilExt.context
import com.zipper.framework.log.event.LoginLogEvent
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.redis.utils.RedisUtils.deleteObject
import com.zipper.framework.redis.utils.RedisUtils.getCacheObject
import com.zipper.framework.tanent.helper.TenantHelper
import com.zipper.framework.web.config.properties.CaptchaProperties
import com.zipper.modules.system.domain.bo.SysUserBo
import com.zipper.modules.system.domain.entity.SysUserEntity
import com.zipper.modules.system.mapper.SysUserMapper
import com.zipper.modules.system.service.user.ISysUserService
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service

/**
 * 注册校验方法
 *
 * @author Lion Li
 */
@Service
class SysRegisterService(
    private val userService: ISysUserService,
    private val userMapper: SysUserMapper,
    private val captchaProperties: CaptchaProperties
) {
    /**
     * 注册
     */
    fun register(registerBody: RegisterBody) {
        val tenantId = registerBody.tenantId ?: ""
        val username = registerBody.username ?: ""
        val password = registerBody.password ?: ""
        // 校验用户类型是否存在
        val userType: String = UserType.getUserType(registerBody.userType).userType

        val captchaEnabled = captchaProperties.enable
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(tenantId, username, registerBody.code ?: "", registerBody.uuid)
        }
        val sysUser = SysUserBo()
        sysUser.userName = username
        sysUser.nickName = username
        sysUser.password = BCrypt.hashpw(password)
        sysUser.userType = userType

        val exist: Boolean = TenantHelper.dynamic(tenantId) {
            userMapper.exists(
                MybatisKt.ktQuery<SysUserEntity>()
                    .eq(SysUserEntity::userName, sysUser.userName)
                    .ne(ObjectUtil.isNotNull(sysUser.userId), SysUserEntity::userId, sysUser.userId)
            )
        }
        if (exist) {
            throw UserException("user.register.save.error", username)
        }
        val regFlag = userService.registerUser(sysUser, tenantId)
        if (!regFlag) {
            throw UserException("user.register.error")
        }
        recordLogininfor(tenantId, username, Constants.REGISTER, message("user.register.success"))
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    fun validateCaptcha(tenantId: String, username: String, code: String, uuid: String?) {
        val verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.defaultString(uuid, "")
        val captcha = getCacheObject<String>(verifyKey)
        deleteObject(verifyKey)
        if (captcha == null) {
            recordLogininfor(tenantId, username, Constants.REGISTER, message("user.jcaptcha.expire"))
            throw CaptchaExpireException()
        }
        if (!code.equals(captcha, ignoreCase = true)) {
            recordLogininfor(tenantId, username, Constants.REGISTER, message("user.jcaptcha.error"))
            throw CaptchaException()
        }
    }

    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    private fun recordLogininfor(tenantId: String, username: String, status: String, message: String) {
        context().publishEvent(
            LoginLogEvent(
                tenantId = tenantId,
                username = username,
                status = status,
                message = message,
                request = ServletUtils.getRequest()
            )
        )
    }
}
