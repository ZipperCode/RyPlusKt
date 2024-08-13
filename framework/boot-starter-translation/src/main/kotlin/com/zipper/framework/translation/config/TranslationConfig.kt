package com.zipper.framework.translation.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.zipper.framework.translation.annotation.TranslationType
import com.zipper.framework.translation.core.TranslationInterface
import com.zipper.framework.translation.core.handler.TranslationBeanSerializerModifier
import com.zipper.framework.translation.core.handler.TranslationHandler
import jakarta.annotation.PostConstruct
import jakarta.annotation.Resource
import org.springframework.boot.autoconfigure.AutoConfiguration
import com.zipper.framework.core.ext.log

/**
 * 翻译模块配置类
 *
 * @author Lion Li
 */
@AutoConfiguration
class TranslationConfig {
    @Resource
    private lateinit var list: List<TranslationInterface<*>>

    @Resource
    private lateinit var objectMapper: ObjectMapper

    @PostConstruct
    fun init() {
        val map = HashMap<String, TranslationInterface<*>>(list.size)
        for (trans in list) {
            if (trans.javaClass.isAnnotationPresent(TranslationType::class.java)) {
                val annotation = trans.javaClass.getAnnotation(TranslationType::class.java)
                map[annotation.type] = trans
            } else {
                log.warn(trans.javaClass.name + " 翻译实现类未标注 TranslationType 注解!")
            }
        }
        TranslationHandler.TRANSLATION_MAPPER.putAll(map)
        // 设置 Bean 序列化修改器
        objectMapper.setSerializerFactory(
            objectMapper.serializerFactory
                .withSerializerModifier(TranslationBeanSerializerModifier())
        )
    }
}
