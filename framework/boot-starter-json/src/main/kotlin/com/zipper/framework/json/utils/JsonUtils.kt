package com.zipper.framework.json.utils

import cn.hutool.core.lang.Dict
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.extra.spring.SpringUtil
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import java.io.IOException

/**
 * JSON 工具类
 *
 * @author 芋道源码
 */
object JsonUtils {
    private val objectMapper: ObjectMapper by lazy {
        SpringUtil.getBean(ObjectMapper::class.java)
    }

    @JvmStatic
    fun toJsonString(target: Any?): String? {
        if (ObjectUtil.isNull(target)) {
            return null
        }
        try {
            return objectMapper.writeValueAsString(target)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun <T> parseObject(text: String?, clazz: Class<T>?): T? {
        if (text.isNullOrEmpty()) {
            return null
        }
        try {
            return objectMapper.readValue(text, clazz)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun <T> parseObject(bytes: ByteArray?, clazz: Class<T>?): T? {
        if (ArrayUtil.isEmpty(bytes)) {
            return null
        }
        try {
            return objectMapper.readValue(bytes, clazz)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun <T> parseObject(text: String?, typeReference: TypeReference<T>?): T? {
        if (text.isNullOrEmpty()) {
            return null
        }
        try {
            return objectMapper.readValue(text, typeReference)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun parseMap(text: String?): Dict? {
        if (text.isNullOrEmpty()) {
            return null
        }
        return try {
            objectMapper.readValue<Dict>(
                text, objectMapper.typeFactory.constructType(
                    Dict::class.java
                )
            )
        } catch (e: MismatchedInputException) {
            // 类型不匹配说明不是json
            null
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun parseArrayMap(text: String?): List<Dict>? {
        if (text.isNullOrEmpty()) {
            return null
        }
        try {
            return objectMapper.readValue(
                text, objectMapper.typeFactory.constructCollectionType(
                    MutableList::class.java, Dict::class.java
                )
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun <T> parseArray(text: String?, clazz: Class<T>?): List<T> {
        if (text.isNullOrEmpty()) {
            return emptyList()
        }
        try {
            return objectMapper.readValue(
                text, objectMapper.typeFactory.constructCollectionType(
                    MutableList::class.java, clazz
                )
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
