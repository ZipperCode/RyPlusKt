package com.zipper.framework.mybatis.core.service

import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils
import com.baomidou.mybatisplus.extension.service.IService
import com.zipper.framework.core.utils.ktext.forceCastClass
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus

interface BaseServicePlus<T, V> : IService<T> {

    fun currentModelClass(): Class<T> {
        return GenericTypeUtils.resolveTypeArguments(this.javaClass, BaseMapperPlus::class.java)[0].forceCastClass()
    }
}