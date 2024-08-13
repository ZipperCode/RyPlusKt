package com.zipper.framework.core.exception.file

import java.io.Serial

/**
 * 文件名大小限制异常类
 *
 * @author ruoyi
 */
class FileSizeLimitExceededException(
    defaultMaxSize: Long
) : FileException("upload.exceed.maxSize", arrayOf(defaultMaxSize)) {
    companion object {
        @Serial
        private val serialVersionUID = 1L
    }
}
