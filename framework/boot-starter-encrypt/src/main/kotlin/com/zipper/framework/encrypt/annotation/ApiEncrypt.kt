package com.zipper.framework.encrypt.annotation

/**
 * 强制加密注解
 *
 * @author Michelle.Chung
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class ApiEncrypt(
    /**
     * 响应加密忽略，默认不加密，为 true 时加密
     */
    val response: Boolean = false
)
