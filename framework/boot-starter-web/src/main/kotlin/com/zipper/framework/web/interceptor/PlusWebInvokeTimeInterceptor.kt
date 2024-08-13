package com.zipper.framework.web.interceptor

import cn.hutool.core.io.IoUtil
import cn.hutool.core.map.MapUtil
import cn.hutool.extra.spring.SpringUtil
import com.alibaba.ttl.TransmittableThreadLocal
import com.zipper.framework.json.utils.JsonUtils
import com.zipper.framework.web.filter.RepeatedlyRequestWrapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.StopWatch
import org.springframework.http.MediaType
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import com.zipper.framework.core.ext.log

/**
 * web的调用时间统计拦截器
 * dev环境有效
 *
 * @author Lion Li
 * @since 3.3.0
 */
@Slf4j
class PlusWebInvokeTimeInterceptor : HandlerInterceptor {
    private val prodProfile = "prod"

    private val invokeTimeTL = TransmittableThreadLocal<StopWatch>()

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (prodProfile != SpringUtil.getActiveProfile()) {
            val url = request.method + " " + request.requestURI

            // 打印请求参数
            if (isJsonRequest(request)) {
                var jsonParam: String? = ""
                if (request is RepeatedlyRequestWrapper) {
                    val reader = request.reader
                    jsonParam = IoUtil.read(reader)
                }
                log.info("[PLUS]开始请求 => URL[{}],参数类型[json],参数:[{}]", url, jsonParam)
            } else {
                val parameterMap = request.parameterMap
                if (MapUtil.isNotEmpty(parameterMap)) {
                    val parameters = JsonUtils.toJsonString(parameterMap)
                    log.info("[PLUS]开始请求 => URL[{}],参数类型[param],参数:[{}]", url, parameters)
                } else {
                    log.info("[PLUS]开始请求 => URL[{}],无参数", url)
                }
            }

            val stopWatch = StopWatch()
            invokeTimeTL.set(stopWatch)
            stopWatch.start()
        }
        return true
    }

    @Throws(Exception::class)
    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
//        String url = request.getMethod() + " " + request.getRequestURI();
//        log.info("[PLUS]postHandle => URL[{}],无参数", url);
    }

    @Throws(Exception::class)
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: java.lang.Exception?
    ) {
        if (prodProfile != SpringUtil.getActiveProfile()) {
            val stopWatch = invokeTimeTL.get()
            if (stopWatch != null) {
                stopWatch.stop()
                log.info(
                    "[PLUS]结束请求 => URL[{}],耗时:[{}]毫秒",
                    request.method + " " + request.requestURI,
                    stopWatch.time
                )
            }
            invokeTimeTL.remove()
        }
    }

    /**
     * 判断本次请求的数据类型是否为json
     *
     * @param request request
     * @return boolean
     */
    private fun isJsonRequest(request: HttpServletRequest): Boolean {
        val contentType = request.contentType
        if (contentType != null) {
            return StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE)
        }
        return false
    }
}
