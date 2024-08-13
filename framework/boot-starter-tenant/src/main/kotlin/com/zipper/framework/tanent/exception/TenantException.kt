package com.zipper.framework.tanent.exception

import com.zipper.framework.core.exception.base.BaseException
import java.io.Serial


/**
 * 租户异常类
 *
 * @author Lion Li
 */
class TenantException(code: String?, vararg args: Any?) : BaseException("tenant", code, args, null) {
    companion object {
        @Serial
        private val serialVersionUID = 1L
    }
}
