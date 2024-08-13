package com.zipper.framework.translation.core.handler

import cn.hutool.core.util.ObjectUtil
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import com.zipper.framework.translation.annotation.Translation
import com.zipper.framework.translation.core.TranslationInterface
import com.zipper.framework.core.utils.StringUtilsExt
import com.zipper.framework.core.utils.reflect.ReflectUtils
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 翻译处理器
 *
 * @author Lion Li
 */
class TranslationHandler : JsonSerializer<Any?>(), ContextualSerializer {
    private var translation: Translation? = null

    @Throws(IOException::class)
    override fun serialize(value: Any?, gen: JsonGenerator, serializers: SerializerProvider) {
        var value = value
        val trans = TRANSLATION_MAPPER[translation!!.type]!!
        if (ObjectUtil.isNotNull(trans)) {
            // 如果映射字段不为空 则取映射字段的值
            if (StringUtilsExt.isNotEmpty(translation!!.mapper)) {
                value = ReflectUtils.invokeGetter(gen.currentValue, translation!!.mapper)
            }
            // 如果为 null 直接写出
            if (ObjectUtil.isNull(value)) {
                gen.writeNull()
                return
            }
            val result = trans.translation(value, translation!!.other)
            gen.writeObject(result)
        } else {
            gen.writeObject(value)
        }
    }

    @Throws(JsonMappingException::class)
    override fun createContextual(prov: SerializerProvider, property: BeanProperty): JsonSerializer<*> {
        val translation = property.getAnnotation(
            Translation::class.java
        )
        if (Objects.nonNull(translation)) {
            this.translation = translation
            return this
        }
        return prov.findValueSerializer(property.type, property)
    }

    companion object {
        /**
         * 全局翻译实现类映射器
         */
        val TRANSLATION_MAPPER: MutableMap<String, TranslationInterface<*>> = ConcurrentHashMap()
    }
}
