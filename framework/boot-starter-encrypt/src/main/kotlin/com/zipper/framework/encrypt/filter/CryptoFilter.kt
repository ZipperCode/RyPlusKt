package com.zipper.framework.encrypt.filter

import cn.hutool.core.util.ObjectUtil
import cn.hutool.extra.spring.SpringUtil
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import com.zipper.framework.core.constant.HttpStatus
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.encrypt.annotation.ApiEncrypt
import com.zipper.framework.encrypt.config.properties.ApiDecryptProperties
import java.io.IOException

/**
 * Crypto 过滤器
 *
 * @author wdhcr
 */
class CryptoFilter(private val properties: ApiDecryptProperties) : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val servletRequest = request as HttpServletRequest
        val servletResponse = response as HttpServletResponse

        var responseFlag = false
        var requestWrapper: ServletRequest? = null
        var responseWrapper: ServletResponse? = null
        var responseBodyWrapper: EncryptResponseBodyWrapper? = null

        // 是否为 json 请求
        if (StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            // 是否为 put 或者 post 请求
            if (HttpMethod.PUT.matches(servletRequest.method) || HttpMethod.POST.matches(servletRequest.method)) {
                // 是否存在加密标头
                val headerValue = servletRequest.getHeader(properties.headerFlag)
                // 获取加密注解
                val apiEncrypt = this.getApiEncryptAnnotation(servletRequest)
                responseFlag = apiEncrypt != null && apiEncrypt.response
                if (StringUtils.isNotBlank(headerValue)) {
                    // 请求解密
                    requestWrapper = DecryptRequestBodyWrapper(servletRequest, properties.privateKey, properties.headerFlag)
                } else {
                    // 是否有注解，有就报错，没有放行
                    if (ObjectUtil.isNotNull(apiEncrypt)) {
                        val exceptionResolver: HandlerExceptionResolver =
                            SpringUtil.getBean("handlerExceptionResolver", HandlerExceptionResolver::class.java)
                        exceptionResolver.resolveException(
                            servletRequest, servletResponse, null,
                            ServiceException("没有访问权限，请联系管理员授权", HttpStatus.FORBIDDEN)
                        )
                        return
                    }
                }
                // 判断是否响应加密
                if (responseFlag) {
                    responseBodyWrapper = EncryptResponseBodyWrapper(servletResponse)
                    responseWrapper = responseBodyWrapper
                }
            }
        }

        chain.doFilter(
            ObjectUtil.defaultIfNull(requestWrapper, request),
            ObjectUtil.defaultIfNull(responseWrapper, response)
        )

        if (responseFlag) {
            servletResponse.reset()
            // 对原始内容加密
            val encryptContent = responseBodyWrapper!!.getEncryptContent(
                servletResponse, properties.publicKey, properties.headerFlag
            )
            // 对加密后的内容写出
            servletResponse.writer.write(encryptContent)
        }
    }

    /**
     * 获取 ApiEncrypt 注解
     */
    private fun getApiEncryptAnnotation(servletRequest: HttpServletRequest): ApiEncrypt? {
        val handlerMapping: RequestMappingHandlerMapping =
            SpringUtil.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping::class.java)
        // 获取注解
        try {
            val mappingHandler = handlerMapping.getHandler(servletRequest)
            if (ObjectUtil.isNotNull(mappingHandler)) {
                val handler = mappingHandler?.handler
                if (ObjectUtil.isNotNull(handler)) {
                    // 从handler获取注解
                    if (handler is HandlerMethod) {
                        return handler.getMethodAnnotation(ApiEncrypt::class.java)
                    }
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return null
    }

    override fun destroy() {
    }
}
