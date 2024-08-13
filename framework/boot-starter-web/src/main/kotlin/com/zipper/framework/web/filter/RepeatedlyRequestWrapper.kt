package com.zipper.framework.web.filter

import cn.hutool.core.io.IoUtil
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import com.zipper.framework.core.constant.Constants
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * 构建可重复读取inputStream的request
 *
 * @author ruoyi
 */
class RepeatedlyRequestWrapper(request: HttpServletRequest, response: ServletResponse) : HttpServletRequestWrapper(request) {
    private val body: ByteArray

    init {
        request.characterEncoding = Constants.UTF8
        response.characterEncoding = Constants.UTF8
        body = IoUtil.readBytes(request.inputStream, false)
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        val bais = ByteArrayInputStream(body)
        return object : ServletInputStream() {
            @Throws(IOException::class)
            override fun read(): Int {
                return bais.read()
            }

            @Throws(IOException::class)
            override fun available(): Int {
                return body.size
            }

            override fun isFinished(): Boolean {
                return false
            }

            override fun isReady(): Boolean {
                return false
            }

            override fun setReadListener(readListener: ReadListener) {
            }
        }
    }
}
