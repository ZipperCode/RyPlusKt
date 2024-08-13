package com.zipper.framework.core.exception.user

import java.io.Serial

/**
 * 验证码错误异常类
 *
 * @author ruoyi
 */
class CaptchaException() : UserException("user.jcaptcha.error") {
    companion object {
        @Serial
        private val serialVersionUID = 1L
    }
}
