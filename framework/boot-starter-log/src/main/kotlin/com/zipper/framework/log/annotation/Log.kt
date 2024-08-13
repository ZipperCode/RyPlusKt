package com.zipper.framework.log.annotation

import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.log.enums.OperatorType

/**
 * 自定义操作日志记录注解
 *
 * @author ruoyi
 */
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.RUNTIME
)
@MustBeDocumented
annotation class Log(
    /**
     * 模块
     */
    val title: String = "",
    /**
     * 功能
     */
    val businessType: BusinessType = BusinessType.OTHER,
    /**
     * 操作人类别
     */
    val operatorType: OperatorType = OperatorType.MANAGE,
    /**
     * 是否保存请求的参数
     */
    val isSaveRequestData: Boolean = true,
    /**
     * 是否保存响应的参数
     */
    val isSaveResponseData: Boolean = true,
    /**
     * 排除指定的请求参数
     */
    val excludeParamNames: Array<String> = []
)
