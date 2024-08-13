package com.zipper.framework.web.config

import com.zipper.framework.web.config.properties.XssProperties
import com.zipper.framework.web.filter.RepeatableFilter
import com.zipper.framework.web.filter.XssFilter
import jakarta.servlet.DispatcherType
import jakarta.servlet.Filter
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import com.zipper.framework.core.utils.StringUtilsExt

/**
 * Filter配置
 *
 * @author Lion Li
 */
@AutoConfiguration
@EnableConfigurationProperties(XssProperties::class)
class FilterConfig {
    @Bean
    @ConditionalOnProperty(value = ["xss.enabled"], havingValue = "true")
    fun xssFilterRegistration(xssProperties: XssProperties): FilterRegistrationBean<*> {
        val registration = FilterRegistrationBean<Filter>()
        registration.setDispatcherTypes(DispatcherType.REQUEST)
        registration.setFilter(XssFilter())
        registration.addUrlPatterns(*StringUtils.split(xssProperties.urlPatterns, StringUtilsExt.SEPARATOR))
        registration.setName("xssFilter")
        registration.order = FilterRegistrationBean.HIGHEST_PRECEDENCE
        val initParameters: MutableMap<String, String?> = HashMap()
        initParameters["excludes"] = xssProperties.excludes
        registration.initParameters = initParameters
        return registration
    }

    @Bean
    fun someFilterRegistration(): FilterRegistrationBean<*> {
        val registration = FilterRegistrationBean<Filter>()
        registration.setFilter(RepeatableFilter())
        registration.addUrlPatterns("/*")
        registration.setName("repeatableFilter")
        registration.order = FilterRegistrationBean.LOWEST_PRECEDENCE
        return registration
    }
}
