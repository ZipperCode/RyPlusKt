package com.zipper.framework.web.config.properties

import com.zipper.framework.web.enums.CaptchaCategory
import com.zipper.framework.web.enums.CaptchaType
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 验证码 配置属性
 *
 * @author Lion Li
 */
@ConfigurationProperties(prefix = "captcha")
class CaptchaProperties {
    var enable: Boolean = true

    /**
     * 验证码类型
     */
    var type: CaptchaType = CaptchaType.MATH

    /**
     * 验证码类别
     */
    var category: CaptchaCategory = CaptchaCategory.LINE

    /**
     * 数字验证码位数
     */
    var numberLength: Int = 4

    /**
     * 字符验证码长度
     */
    var charLength: Int = 4
}
