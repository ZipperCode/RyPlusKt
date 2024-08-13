package com.zipper.framework.core.exception.file

import com.zipper.framework.core.exception.base.BaseException
import java.io.Serial


/**
 * 文件信息异常类
 *
 * @author ruoyi
 */
open class FileException(code: String?, args: Array<Any?>?) : BaseException("file", code, args, null) {
    companion object {
        @Serial
        private val serialVersionUID = 1L
    }
}
