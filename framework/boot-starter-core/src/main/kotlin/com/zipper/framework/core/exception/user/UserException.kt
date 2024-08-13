package com.zipper.framework.core.exception.user

import com.zipper.framework.core.exception.base.BaseException
import java.io.Serial


/**
 * 用户信息异常类
 *
 * @author ruoyi
 */
open class UserException(code: String?, vararg args: Any?) : BaseException("user", code, args, null) {
    companion object {
        @Serial
        private val serialVersionUID = 1L
    }
}
