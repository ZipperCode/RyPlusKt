package com.zipper.framework.mybatis.core

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import kotlin.reflect.KProperty1

object MybatisKt {

    inline fun <reified T : kotlin.Any> ktQuery(): KtQueryWrapper<T> = KtQueryWrapper(T::class.java)

    inline fun <reified T : kotlin.Any> ktUpdate(): KtUpdateWrapper<T> = KtUpdateWrapper(T::class.java)
}

inline fun <reified T : Any> KtQueryWrapper<T>.betweenIfPresent(property: KProperty1<T, *>, values: Array<*>?): KtQueryWrapper<T> {
    val value1 = values?.getOrNull(0)
    val value2 = values?.getOrNull(1)
    return betweenIfPresent(property, value1, value2)
}

inline fun <reified T : Any> KtQueryWrapper<T>.betweenIfPresent(property: KProperty1<T, *>, value1: Any?, value2: Any?): KtQueryWrapper<T> {
    if (value1 != null && value2 != null) {
        return between(property, value1, value2)
    }
    if (value1 != null) {
        return ge(property, value1)
    }

    if (value2 != null) {
        return le(property, value2)
    }
    return this
}

inline fun <reified T: Any> KtQueryWrapper<T>.likeIfPresent(property: KProperty1<T, *>, value: T?): KtQueryWrapper<T> {
    if (value != null) {
        return like(property, value)
    }
    return this
}

inline fun <reified T: Any> KtQueryWrapper<T>.likeIfPresent(property: KProperty1<T, *>, value: String?): KtQueryWrapper<T> {
    if (!value.isNullOrEmpty()) {
        return like(property, value)
    }
    return this
}



