package com.zipper.framework.security.handler

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.exception.NotPermissionException
import cn.dev33.satoken.exception.NotRoleException
import cn.hutool.http.HttpStatus
import com.zipper.framework.core.domain.R
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.exception.base.BaseException
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.StreamUtils

/**
 * 全局异常处理器
 *
 * @author Lion Li
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException::class)
    fun handleNotPermissionException(e: NotPermissionException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.message)
        return R.fail(HttpStatus.HTTP_FORBIDDEN, "没有访问权限，请联系管理员授权")
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException::class)
    fun handleNotRoleException(e: NotRoleException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.message)
        return R.fail(HttpStatus.HTTP_FORBIDDEN, "没有访问权限，请联系管理员授权")
    }

    /**
     * 认证失败
     */
    @ExceptionHandler(NotLoginException::class)
    fun handleNotLoginException(e: NotLoginException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',认证失败'{}',无法访问系统资源", requestURI, e.message)
        return R.fail(HttpStatus.HTTP_UNAUTHORIZED, "认证失败，无法访问系统资源")
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupported(
        e: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest
    ): R<String> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.method)
        return R.fail(e.message)
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException::class)
    fun handleServiceException(e: ServiceException, request: HttpServletRequest?): R<Void> {
        log.error(e.message)
        return R.fail(e.code, e.message)
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException, request: HttpServletRequest?): R<String> {
        log.error(e.message)
        return R.fail(e.message)
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException::class)
    fun handleMissingPathVariableException(e: MissingPathVariableException, request: HttpServletRequest): R<String> {
        val requestURI = request.requestURI
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI)
        return R.fail(String.format("请求路径中缺少必需的路径变量[%s]", e.variableName))
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI)
        return R.fail(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.name, e.requiredType.name, e.value))
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException, request: HttpServletRequest): R<String> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',发生未知异常.", requestURI, e)
        return R.fail(e.message)
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): R<String> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',发生系统异常.", requestURI, e)
        return R.fail(e.message)
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): R<String> {
        log.error(e.message)
        val message: String = StreamUtils.join(e.allErrors, DefaultMessageSourceResolvable::getDefaultMessage, ", ")
        return R.fail(message)
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolationException(e: ConstraintViolationException): R<String> {
        log.error(e.message)
        val message: String = StreamUtils.join(e.constraintViolations, { obj: ConstraintViolation<*> -> obj.message }, ", ")
        return R.fail(message)
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): R<String> {
        log.error(e.message)
        val message = e.bindingResult.fieldError?.defaultMessage
        return R.fail(message)
    }
}
