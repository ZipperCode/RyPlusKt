package com.zipper.framework.web.config

import com.zipper.framework.web.core.I18nLocaleResolver
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.LocaleResolver

/**
 * 国际化配置
 *
 * @author Lion Li
 */
@AutoConfiguration(before = [WebMvcAutoConfiguration::class])
class I18nConfig {
    @Bean
    fun localeResolver(): LocaleResolver {
        return I18nLocaleResolver()
    }
}
