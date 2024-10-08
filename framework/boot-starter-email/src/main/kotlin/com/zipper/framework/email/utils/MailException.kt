package com.zipper.framework.email.utils

import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.core.util.StrUtil
import java.io.Serial

/**
 * 邮件异常
 *
 * @author xiaoleilu
 */
class MailException : RuntimeException {
    constructor(e: Throwable?) : super(ExceptionUtil.getMessage(e), e)

    constructor(message: String?) : super(message)

    constructor(messageTemplate: String?, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))

    constructor(message: String?, throwable: Throwable?) : super(message, throwable)

    constructor(message: String?, throwable: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        throwable,
        enableSuppression,
        writableStackTrace
    )

    constructor(throwable: Throwable?, messageTemplate: String?, vararg params: Any?) : super(
        StrUtil.format(messageTemplate, *params),
        throwable
    )

    companion object {
        @Serial
        private val serialVersionUID = 8247610319171014183L
    }
}
