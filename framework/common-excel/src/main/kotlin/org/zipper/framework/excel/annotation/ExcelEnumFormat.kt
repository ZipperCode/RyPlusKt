package org.zipper.framework.excel.annotation

import java.lang.annotation.Inherited
import kotlin.reflect.KClass

/**
 * 枚举格式化
 *
 * @author Liang
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class ExcelEnumFormat(
    /**
     * 字典枚举类型
     */
    val enumClass: KClass<out Enum<*>>,
    /**
     * 字典枚举类中对应的code属性名称，默认为code
     */
    val codeField: String = "code",
    /**
     * 字典枚举类中对应的text属性名称，默认为text
     */
    val textField: String = "text"
)
