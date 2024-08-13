package com.zipper.server.web.controller

import cn.dev33.satoken.annotation.SaIgnore
import cn.hutool.captcha.AbstractCaptcha
import cn.hutool.captcha.generator.CodeGenerator
import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.ReflectUtil
import com.zipper.framework.core.constant.Constants
import com.zipper.framework.core.constant.GlobalConstants
import com.zipper.framework.core.domain.R
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.framework.core.utils.reflect.ReflectUtils
import com.zipper.framework.email.config.properties.MailProperties
import com.zipper.framework.email.utils.MailUtils
import com.zipper.framework.ratelimiter.annotation.RateLimiter
import com.zipper.framework.ratelimiter.enums.LimitType
import com.zipper.framework.redis.utils.RedisUtils.setCacheObject
import com.zipper.framework.web.config.properties.CaptchaProperties
import com.zipper.framework.web.enums.CaptchaType
import com.zipper.server.web.domain.vo.CaptchaVo
import jakarta.validation.constraints.NotBlank
import org.apache.commons.lang3.StringUtils
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

/**
 * 验证码操作处理
 *
 * @author Lion Li
 */
@SaIgnore
@Validated
@RestController
class CaptchaController(
    private val captchaProperties: CaptchaProperties,
    private val mailProperties: MailProperties
) {
    /**
     * 短信验证码
     *
     * @param phonenumber 用户手机号
     */
    @RateLimiter(key = "#phonenumber", time = 60, count = 1)
    @GetMapping("/resource/sms/code")
    fun smsCode(phonenumber: @NotBlank(message = "{user.phonenumber.not.blank}") String?): R<Void> {
        val key = GlobalConstants.CAPTCHA_CODE_KEY + phonenumber
        val code = RandomUtil.randomNumbers(4)
//        setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION.toLong()))
//        // 验证码模板id 自行处理 (查数据库或写死均可)
//        val templateId = ""
//        val map = LinkedHashMap<String, String>(1)
//        map["code"] = code
//        val smsBlend: SmsBlend = SmsFactory.createSmsBlend(SupplierType.ALIBABA)
//        val smsResponse: SmsResponse = smsBlend.sendMessage(phonenumber, templateId, map)
//        if ("OK" != smsResponse.getCode()) {
//            log.error("验证码短信发送异常 => {}", smsResponse)
//            return R.fail(smsResponse.getMessage())
//        }
        return R.ok()
    }

    /**
     * 邮箱验证码
     *
     * @param email 邮箱
     */
    @RateLimiter(key = "#email", time = 60, count = 1)
    @GetMapping("/resource/email/code")
    fun emailCode(email: @NotBlank(message = "{user.email.not.blank}") String?): R<Void> {
        if (!mailProperties.enabled) {
            return R.fail("当前系统没有开启邮箱功能！")
        }
        val key = GlobalConstants.CAPTCHA_CODE_KEY + email
        val code = RandomUtil.randomNumbers(4)
        setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION.toLong()))
        try {
            MailUtils.sendText(
                email,
                "登录验证码",
                "您本次验证码为：" + code + "，有效性为" + Constants.CAPTCHA_EXPIRATION + "分钟，请尽快填写。"
            )
        } catch (e: Exception) {
            log.error("验证码短信发送异常 => {}", e.message)
            return R.fail<Void>(e.message)
        }
        return R.ok()
    }

    @GetMapping("/auth/code")
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    fun getCode(): R<Any> {
        val captchaVo: CaptchaVo = CaptchaVo()
        val captchaEnabled = captchaProperties.enable
        if (!captchaEnabled) {
            captchaVo.captchaEnabled = false
            return R.ok(captchaVo)
        }
        // 保存验证码信息
        val uuid = IdUtil.simpleUUID()
        val verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid
        // 生成验证码
        val captchaType: CaptchaType = captchaProperties.type
        val isMath = CaptchaType.MATH === captchaType
        val length = if (isMath) captchaProperties.numberLength else captchaProperties.charLength
        val codeGenerator: CodeGenerator = ReflectUtil.newInstance(captchaType.clazz, length)
        val captcha = SpringUtilExt.getBeanByType<AbstractCaptcha>(captchaProperties.category.clazz)
        captcha.generator = codeGenerator
        captcha.createCode()
        var code = captcha.code
        if (isMath) {
            val parser: ExpressionParser = SpelExpressionParser()
            val exp = parser.parseExpression(StringUtils.remove(code, "="))
            code = exp.getValue(String::class.java)
        }
        setCacheObject(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION.toLong()))
        captchaVo.uuid = uuid
        captchaVo.img = captcha.imageBase64
        return R.ok(captchaVo)
    }
}
