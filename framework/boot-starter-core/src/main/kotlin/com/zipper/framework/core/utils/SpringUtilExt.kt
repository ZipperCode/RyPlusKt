package com.zipper.framework.core.utils

import cn.hutool.extra.spring.SpringUtil
import org.springframework.aop.framework.AopContext
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

/**
 * spring工具类
 *
 * @author Lion Li
 */
@Component
object SpringUtilExt : SpringUtil() {
    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     */
    @JvmStatic
    fun containsBean(name: String): Boolean {
        return getBeanFactory().containsBean(name)
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     */
    @JvmStatic
    @Throws(NoSuchBeanDefinitionException::class)
    fun isSingleton(name: String): Boolean {
        return getBeanFactory().isSingleton(name)
    }

    /**
     * @return Class 注册对象的类型
     */
    @JvmStatic
    @Throws(NoSuchBeanDefinitionException::class)
    fun getType(name: String): Class<*>? {
        return getBeanFactory().getType(name)
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     */
    @JvmStatic
    @Throws(NoSuchBeanDefinitionException::class)
    fun getAliases(name: String): Array<String> {
        return getBeanFactory().getAliases(name)
    }

    /**
     * 获取aop代理对象
     */
    @JvmStatic
    inline fun <reified T> getAopProxy(invoker: T): T {
        return AopContext.currentProxy() as T
    }


    /**
     * 获取spring上下文
     */
    @JvmStatic
    fun context(): ApplicationContext {
        return getApplicationContext()
    }

    inline fun <reified T> getBeanByName(name: String): T {
        return getBeanFactory().getBean(name) as T
    }

    inline fun <reified T> getBeanByType(clazz: Class<out T>): T {
        return getBeanFactory().getBean(clazz)
    }
}
