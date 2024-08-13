package com.zipper.framework.core.exception.file

import java.io.Serial

/**
 * 文件名称超长限制异常类
 *
 * @author ruoyi
 */
class FileNameLengthLimitExceededException(
    defaultFileNameLength: Int
) : FileException("upload.filename.exceed.length", arrayOf(defaultFileNameLength)) {
    companion object {
        @Serial
        private val serialVersionUID = 1L
    }
}
