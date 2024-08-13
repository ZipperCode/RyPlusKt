package com.zipper.framework.core.domain

import com.zipper.framework.core.constant.HttpStatus
import java.io.Serial
import java.io.Serializable

/**
 * 响应信息主体
 *
 * @author Lion Li
 */
class R<T> @JvmOverloads constructor(
    val code: Int = 0,

    val msg: String? = null,

    val data: T? = null

) : Serializable {

    fun isError(): Boolean {
        return !isSuccess()
    }

    fun isSuccess(): Boolean {
        return SUCCESS == this.code
    }

    companion object {
        @Serial
        private val serialVersionUID = 1L

        /**
         * 成功
         */
        const val SUCCESS: Int = 200

        /**
         * 失败
         */
        const val FAIL: Int = 500

        fun <T> ok(): R<T> {
            return restResult(null, SUCCESS, "操作成功")
        }

        fun <T> ok(data: T): R<T> {
            return restResult(data, SUCCESS, "操作成功")
        }

        fun <T> ok(msg: String): R<T> {
            return restResult(null, SUCCESS, msg)
        }

        fun <T> ok(msg: String, data: T): R<T> {
            return restResult(data, SUCCESS, msg)
        }

        fun <T> fail(): R<T> {
            return restResult(null, FAIL, "操作失败")
        }

        fun <T> fail(msg: String?): R<T> {
            return restResult(null, FAIL, msg)
        }

        fun <T> fail(data: T?): R<T> {
            return restResult(data, FAIL, "操作失败")
        }

        fun <T> fail(msg: String, data: T): R<T> {
            return restResult(data, FAIL, msg)
        }

        fun <T> fail(code: Int, msg: String): R<T> {
            return restResult(null, code, msg)
        }

        /**
         * 返回警告消息
         *
         * @param msg 返回内容
         * @return 警告消息
         */
        fun <T> warn(msg: String): R<T> {
            return restResult(null, HttpStatus.WARN, msg)
        }

        /**
         * 返回警告消息
         *
         * @param msg  返回内容
         * @param data 数据对象
         * @return 警告消息
         */
        fun <T> warn(msg: String, data: T): R<T> {
            return restResult(data, HttpStatus.WARN, msg)
        }

        private fun <T> restResult(data: T?, code: Int, msg: String?): R<T> {
            val r = R<T>(code, msg, data)
            return r
        }

    }
}
