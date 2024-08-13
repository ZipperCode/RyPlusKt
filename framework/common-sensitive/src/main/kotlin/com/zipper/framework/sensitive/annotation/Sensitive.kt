package com.zipper.framework.sensitive.annotation

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.zipper.framework.sensitive.core.SensitiveStrategy
import com.zipper.framework.sensitive.handler.SensitiveHandler

/**
 * 数据脱敏注解
 *
 * @author zhujie
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveHandler::class)
annotation class Sensitive(
    val strategy: SensitiveStrategy,
    val roleKey: String = "",
    val perms: String = ""
)
