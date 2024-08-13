package com.zipper.framework.translation.annotation

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.zipper.framework.translation.core.handler.TranslationHandler
import java.lang.annotation.Inherited

/**
 * 通用翻译注解
 *
 * @author Lion Li
 */
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MustBeDocumented
@JacksonAnnotationsInside
@JsonSerialize(using = TranslationHandler::class)
annotation class Translation(
    /**
     * 类型 (需与实现类上的 [TranslationType] 注解type对应)
     *
     *
     * 默认取当前字段的值 如果设置了 @[Translation.mapper] 则取映射字段的值
     */
    val type: String,
    /**
     * 映射字段 (如果不为空则取此字段的值)
     */
    val mapper: String = "",
    /**
     * 其他条件 例如: 字典type(sys_user_sex)
     */
    val other: String = ""
)
