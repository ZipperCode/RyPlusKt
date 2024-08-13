//package com.zipper.framework.mybatis.core.mapper
//
//import cn.hutool.core.collection.CollUtil
//import cn.hutool.core.util.ObjectUtil
//import com.baomidou.mybatisplus.core.conditions.Wrapper
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
//import com.baomidou.mybatisplus.core.mapper.BaseMapper
//import com.baomidou.mybatisplus.core.metadata.IPage
//import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page
//import com.baomidou.mybatisplus.extension.toolkit.Db
//import org.apache.ibatis.logging.Log
//import org.apache.ibatis.logging.LogFactory
//import com.zipper.framework.core.utils.MapstructUtils
//import com.zipper.framework.core.utils.ktext.forceCast
//import com.zipper.framework.core.utils.ktext.forceCastClass
//import org.apache.ibatis.annotations.Mapper
//import java.io.Serializable
//import java.util.*
//import java.util.function.Function
//import java.util.stream.Collectors
//
///**
// * 自定义 Mapper 接口, 实现 自定义扩展
// *
// * @param <T> table 泛型
// * @param <V> vo 泛型
// * @author Lion Li
// * @since 2021-05-13
//</V></T> */
//@Mapper
//interface BaseMapperPlusVo<T, V> : BaseMapperPlus<T> {
//
//    fun currentVoClass(): Class<V> {
//        GenericTypeUtils.resolveTypeArguments(this.javaClass, BaseMapperPlusVo::class.java)
//        return GenericTypeUtils.resolveTypeArguments(this.javaClass, BaseMapperPlusVo::class.java)[1].forceCastClass()
//    }
//
//    fun selectVoById(id: Serializable?): V? {
//        return selectVoById(id, this.currentVoClass())
//    }
//
//    fun selectVoBatchIds(idList: Collection<Serializable?>?): List<V> {
//        return selectVoBatchIds(idList, this.currentVoClass())
//    }
//
//    fun selectVoByMap(map: Map<String?, Any?>?): List<V> {
//        return selectVoByMap(map, this.currentVoClass())
//    }
//
//    fun selectVoOne(wrapper: Wrapper<T>): V? {
//        return selectVoOne(wrapper, this.currentVoClass())
//    }
//
//    fun selectVoList(): List<V> {
//        return selectVoList(QueryWrapper(), this.currentVoClass())
//    }
//
//    fun selectVoList(wrapper: Wrapper<T>): List<V> {
//        return selectVoList(wrapper, this.currentVoClass())
//    }
//
//    fun <P : IPage<V>?> selectVoPage(page: IPage<T>, wrapper: Wrapper<T>): P {
//        return selectVoPage(page, wrapper, this.currentVoClass())
//    }
//}
