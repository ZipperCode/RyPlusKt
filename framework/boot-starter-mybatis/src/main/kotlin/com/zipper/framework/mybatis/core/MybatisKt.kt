package com.zipper.framework.mybatis.core

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper

object MybatisKt {

    inline fun <reified T: kotlin.Any> ktQuery(): KtQueryWrapper<T> = KtQueryWrapper(T::class.java)

    inline fun <reified T: kotlin.Any> ktUpdate(): KtUpdateWrapper<T> = KtUpdateWrapper(T::class.java)
}