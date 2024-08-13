package com.zipper.framework.oss.exception

import java.io.Serial

/**
 * OSS异常类
 *
 * @author Lion Li
 */
class OssException(msg: String?) : RuntimeException(msg) {
    companion object {
        @Serial
        private val serialVersionUID = 1L
    }
}
