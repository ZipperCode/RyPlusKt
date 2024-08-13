package com.zipper.framework.core.utils

import cn.hutool.core.convert.Convert
import cn.hutool.extra.servlet.JakartaServletUtil
import cn.hutool.http.HttpStatus
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.apache.commons.lang3.StringUtils
import org.springframework.http.MediaType
import org.springframework.util.LinkedCaseInsensitiveMap
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 客户端工具类
 *
 * @author ruoyi
 */
object ServletUtils : JakartaServletUtil() {
    /**
     * 获取String参数
     */
    @JvmStatic
    fun getParameter(name: String?): String? = getRequest()?.getParameter(name)

    /**
     * 获取String参数
     */
    @JvmStatic
    fun getParameter(name: String?, defaultValue: String): String {
        return getRequest()?.getParameter(name) ?: defaultValue
    }

    /**
     * 获取Integer参数
     */
    @JvmStatic
    fun getParameterToInt(name: String?): Int {
        return Convert.toInt(getRequest()?.getParameter(name), 0)
    }

    /**
     * 获取Integer参数
     */
    @JvmStatic
    fun getParameterToInt(name: String?, defaultValue: Int?): Int {
        return Convert.toInt(getRequest()?.getParameter(name), defaultValue)
    }

    /**
     * 获取Boolean参数
     */
    @JvmStatic
    fun getParameterToBool(name: String?): Boolean {
        return Convert.toBool(getRequest()?.getParameter(name), false)
    }

    /**
     * 获取Boolean参数
     */
    @JvmStatic
    fun getParameterToBool(name: String?, defaultValue: Boolean?): Boolean {
        return Convert.toBool(getRequest()?.getParameter(name), defaultValue)
    }

    @JvmStatic
    fun getRequest(): HttpServletRequest? = try {
        getRequestAttributes()?.request
    } catch (e: Exception) {
        null
    }

    @JvmStatic
    fun getResponse(): HttpServletResponse? = try {
        getRequestAttributes()?.response
    } catch (e: Exception) {
        null
    }

    /**
     * 获取session
     */
    @JvmStatic
    fun getSession(): HttpSession? = getRequest()?.session

    @JvmStatic
    fun getRequestAttributes(): ServletRequestAttributes? {
        return try {
            RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic
    fun getHeader(request: HttpServletRequest, name: String?): String {
        val value = request.getHeader(name)
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY
        }
        return urlDecode(value)
    }

    @JvmStatic
    fun getHeaders(request: HttpServletRequest): Map<String, String> {
        val map: MutableMap<String, String> = LinkedCaseInsensitiveMap()
        val enumeration = request.headerNames
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                val key = enumeration.nextElement()
                val value = request.getHeader(key)
                map[key] = value
            }
        }
        return map
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     */
    @JvmStatic
    fun renderString(response: HttpServletResponse, string: String?) {
        try {
            response.status = HttpStatus.HTTP_OK
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.toString()
            response.writer.print(string)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 是否是Ajax异步请求
     *
     * @param request
     */
    @JvmStatic
    fun isAjaxRequest(request: HttpServletRequest): Boolean {
        val accept = request.getHeader("accept")
        if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            return true
        }

        val xRequestedWith = request.getHeader("X-Requested-With")
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest")) {
            return true
        }

        val uri = request.requestURI
        if (StringUtils.equalsAnyIgnoreCase(uri, ".json", ".xml")) {
            return true
        }

        val ajax = request.getParameter("__ajax")
        return StringUtils.equalsAnyIgnoreCase(ajax, "json", "xml")
    }

    @JvmStatic
    fun getClientIP() = getClientIP(getRequest())

    /**
     * 内容编码
     *
     * @param str 内容
     * @return 编码后的内容
     */
    @JvmStatic
    fun urlEncode(str: String?): String {
        return URLEncoder.encode(str, StandardCharsets.UTF_8)
    }

    /**
     * 内容解码
     *
     * @param str 内容
     * @return 解码后的内容
     */
    @JvmStatic
    fun urlDecode(str: String?): String {
        return URLDecoder.decode(str, StandardCharsets.UTF_8)
    }
}
