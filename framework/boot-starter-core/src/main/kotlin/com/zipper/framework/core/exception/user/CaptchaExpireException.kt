package com.zipper.framework.core.exception.user

import java.io.Serial

/**
 * 验证码失效异常类
 *
 * @author ruoyi
 */
class CaptchaExpireException() : UserException("user.jcaptcha.expire") {

    companion object {
        @Serial
        private val serialVersionUID = 1L
    }
}
