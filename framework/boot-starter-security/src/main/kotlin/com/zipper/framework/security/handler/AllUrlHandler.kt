package com.zipper.framework.security.handler

import cn.hutool.core.util.ReUtil
import cn.hutool.extra.spring.SpringUtil
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.function.Consumer
import java.util.regex.Pattern

/**
 * 获取所有Url配置
 *
 * @author Lion Li
 */
@Component
class AllUrlHandler : InitializingBean {
    val urls: MutableList<String> = ArrayList()

    override fun afterPropertiesSet() {
        val set: MutableSet<String> = HashSet()
        val mapping = SpringUtil.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping::class.java)
        mapping.handlerMethods.keys.forEach(Consumer { info: RequestMappingInfo ->
            // 获取注解上边的 path 替代 path variable 为 *
            info.pathPatternsCondition?.patterns?.forEach {
                set.add(ReUtil.replaceAll(it.patternString, PATTERN, "*"))
            }
        })
        urls.addAll(set)
    }

    companion object {
        private val PATTERN: Pattern = Pattern.compile("\\{(.*?)\\}")
    }
}
