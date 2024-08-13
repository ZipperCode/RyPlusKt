package com.zipper.framework.mybatis.handler

import com.zipper.framework.core.domain.R
import jakarta.servlet.http.HttpServletRequest
import org.mybatis.spring.MyBatisSystemException
import org.springframework.dao.DuplicateKeyException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import com.zipper.framework.core.ext.log

/**
 * Mybatis异常处理器
 *
 * @author Lion Li
 */
@RestControllerAdvice
class MybatisExceptionHandler {
    /**
     * 主键或UNIQUE索引，数据重复异常
     */
    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKeyException(e: DuplicateKeyException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        log.error("请求地址'{}',数据库中已存在记录'{}'", requestURI, e.message)
        return R.fail("数据库中已存在该记录，请联系管理员确认")
    }

    /**
     * Mybatis系统异常 通用处理
     */
    @ExceptionHandler(MyBatisSystemException::class)
    fun handleCannotFindDataSourceException(e: MyBatisSystemException, request: HttpServletRequest): R<Void> {
        val requestURI = request.requestURI
        val message = e.message ?: ""
        e.printStackTrace()
        if ("CannotFindDataSourceException".contains(message)) {
            log.error("请求地址'{}', 未找到数据源", requestURI)
            return R.fail("未找到数据源，请联系管理员确认")
        }
        log.error("请求地址'{}', Mybatis系统异常", requestURI, e)
        return R.fail(message)
    }
}
