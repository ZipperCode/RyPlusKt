package com.zipper.framework.mybatis.annotation

/**
 * 数据权限
 *
 * 一个注解只能对应一个模板
 *
 * @author Lion Li
 * @version 3.5.0
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DataColumn(
    /**
     * 占位符关键字
     */
    val key: Array<String> = ["deptName"],
    /**
     * 占位符替换值
     */
    vararg val value: String = ["dept_id"]
)
