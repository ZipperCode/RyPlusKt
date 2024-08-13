package com.zipper.framework.core.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.EnableAspectJAutoProxy
import com.zipper.framework.core.config.properties.RuoYiProperties

/**
 * 程序注解配置
 *
 * @author Lion Li
 */
@AutoConfiguration
@EnableConfigurationProperties(RuoYiProperties::class)
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
class ApplicationConfig
