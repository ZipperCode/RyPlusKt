package com.zipper.framework.encrypt.filter

import cn.hutool.core.util.RandomUtil
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.WriteListener
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper
import com.zipper.framework.encrypt.utils.EncryptUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.charset.StandardCharsets

/**
 * 加密响应参数包装类
 *
 * @author Michelle.Chung
 */
class EncryptResponseBodyWrapper(response: HttpServletResponse) : HttpServletResponseWrapper(response) {
    private val byteArrayOutputStream = ByteArrayOutputStream()
    private val servletOutputStream: ServletOutputStream?
    private val printWriter: PrintWriter

    init {
        this.servletOutputStream = this.outputStream
        this.printWriter = PrintWriter(OutputStreamWriter(byteArrayOutputStream))
    }

    override fun getWriter(): PrintWriter {
        return printWriter
    }

    @Throws(IOException::class)
    override fun flushBuffer() {
        servletOutputStream?.flush()
        printWriter.flush()
    }

    override fun reset() {
        byteArrayOutputStream.reset()
    }

    @get:Throws(IOException::class)
    val responseData: ByteArray
        get() {
            flushBuffer()
            return byteArrayOutputStream.toByteArray()
        }

    @get:Throws(IOException::class)
    val content: String
        get() {
            flushBuffer()
            return byteArrayOutputStream.toString()
        }

    /**
     * 获取加密内容
     *
     * @param servletResponse response
     * @param publicKey       RSA公钥 (用于加密 AES 秘钥)
     * @param headerFlag      请求头标志
     * @return 加密内容
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getEncryptContent(servletResponse: HttpServletResponse, publicKey: String?, headerFlag: String?): String {
        // 生成秘钥
        val aesPassword = RandomUtil.randomString(32)
        // 秘钥使用 Base64 编码
        val encryptAes: String = EncryptUtils.encryptByBase64(aesPassword)
        // Rsa 公钥加密 Base64 编码
        val encryptPassword: String = EncryptUtils.encryptByRsa(encryptAes, publicKey)

        // 设置响应头
        servletResponse.setHeader(headerFlag, encryptPassword)
        servletResponse.setHeader("Access-Control-Allow-Origin", "*")
        servletResponse.setHeader("Access-Control-Allow-Methods", "*")
        servletResponse.characterEncoding = StandardCharsets.UTF_8.toString()

        // 获取原始内容
        val originalBody = this.content
        // 对内容进行加密
        return EncryptUtils.encryptByAes(originalBody, aesPassword)
    }

    @Throws(IOException::class)
    override fun getOutputStream(): ServletOutputStream {
        return object : ServletOutputStream() {
            override fun isReady(): Boolean {
                return false
            }

            override fun setWriteListener(writeListener: WriteListener) {
            }

            @Throws(IOException::class)
            override fun write(b: Int) {
                byteArrayOutputStream.write(b)
            }

            @Throws(IOException::class)
            override fun write(b: ByteArray) {
                byteArrayOutputStream.write(b)
            }

            @Throws(IOException::class)
            override fun write(b: ByteArray, off: Int, len: Int) {
                byteArrayOutputStream.write(b, off, len)
            }
        }
    }
}
