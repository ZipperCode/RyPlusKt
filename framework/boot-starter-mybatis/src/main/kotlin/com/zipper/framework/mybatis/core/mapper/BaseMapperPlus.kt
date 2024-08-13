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
//import com.zipper.framework.core.utils.MapstructUtils
//import com.zipper.framework.core.utils.ktext.forceCast
//import com.zipper.framework.core.utils.ktext.forceCastClass
//import org.apache.ibatis.annotations.Mapper
//import java.io.Serializable
//import java.util.function.Function
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
//interface BaseMapperPlus<T> : BaseMapper<T> {
//
//
//    fun currentModelClass(): Class<T> {
//        return GenericTypeUtils.resolveTypeArguments(this.javaClass, BaseMapperPlus::class.java)[0].forceCastClass()
//    }
//
//    fun selectList(): List<T> {
//        return this.selectList(QueryWrapper())
//    }
//
//    /**
//     * 批量插入
//     */
//    fun insertBatch(entityList: Collection<T>): Boolean {
//        return Db.saveBatch(entityList)
//    }
//
//    /**
//     * 批量更新
//     */
//    fun updateBatchById(entityList: Collection<T>): Boolean {
//        return Db.updateBatchById(entityList)
//    }
//
//    /**
//     * 批量插入或更新
//     */
//    fun insertOrUpdateBatch(entityList: Collection<T>): Boolean {
//        return Db.saveOrUpdateBatch(entityList)
//    }
//
//    /**
//     * 批量插入(包含限制条数)
//     */
//    fun insertBatch(entityList: Collection<T>, batchSize: Int): Boolean {
//        return Db.saveBatch(entityList, batchSize)
//    }
//
//    /**
//     * 批量更新(包含限制条数)
//     */
//    fun updateBatchById(entityList: Collection<T>, batchSize: Int): Boolean {
//        return Db.updateBatchById(entityList, batchSize)
//    }
//
//    /**
//     * 批量插入或更新(包含限制条数)
//     */
//    fun insertOrUpdateBatch(entityList: Collection<T>, batchSize: Int): Boolean {
//        return Db.saveOrUpdateBatch(entityList, batchSize)
//    }
//
//    /**
//     * 插入或更新(包含限制条数)
//     */
//    fun insertOrUpdate(entity: T): Boolean {
//        return Db.saveOrUpdate(entity)
//    }
//
//
//    /**
//     * 根据 ID 查询
//     */
//    fun <C> selectVoById(id: Serializable?, voClass: Class<C>): C? {
//        val obj = this.selectById(id)
//        if (ObjectUtil.isNull(obj)) {
//            return null
//        }
//        return MapstructUtils.convert(obj, voClass)
//    }
//
//
//    /**
//     * 查询（根据ID 批量查询）
//     */
//    fun <C> selectVoBatchIds(idList: Collection<Serializable?>?, voClass: Class<C>?): List<C> {
//        val list = this.selectBatchIds(idList)
//        if (CollUtil.isEmpty(list)) {
//            return CollUtil.newArrayList()
//        }
//        return MapstructUtils.convertWithClass(list, voClass)
//    }
//
//    /**
//     * 查询（根据 columnMap 条件）
//     */
//    fun <C> selectVoByMap(map: Map<String?, Any?>?, voClass: Class<C>): List<C> {
//        val list = this.selectByMap(map)
//        if (CollUtil.isEmpty(list)) {
//            return CollUtil.newArrayList()
//        }
//        return MapstructUtils.convertWithClass(list, voClass)
//    }
//
//
//    /**
//     * 根据 entity 条件，查询一条记录
//     */
//    fun <C> selectVoOne(wrapper: Wrapper<T>, voClass: Class<C>): C? {
//        val obj = this.selectOne(wrapper)
//        if (ObjectUtil.isNull(obj)) {
//            return null
//        }
//        return MapstructUtils.convert(obj, voClass)
//    }
//
//
//    /**
//     * 根据 entity 条件，查询全部记录
//     */
//    fun <C> selectVoList(wrapper: Wrapper<T>, voClass: Class<C>): List<C> {
//        val list = this.selectList(wrapper)
//        if (CollUtil.isEmpty(list)) {
//            return CollUtil.newArrayList()
//        }
//        return MapstructUtils.convertWithClass(list, voClass)
//    }
//
//    /**
//     * 分页查询VO
//     */
//    fun <C, P : IPage<C>?> selectVoPage(page: IPage<T>, wrapper: Wrapper<T>, voClass: Class<C>): P {
//        val list = this.selectList(page, wrapper)
//        val voPage: IPage<C> = Page(page.current, page.size, page.total)
//        if (CollUtil.isEmpty(list)) {
//            return voPage.forceCast()
//        }
//        voPage.setRecords(MapstructUtils.convertWithClass(list, voClass))
//        return voPage.forceCast()
//    }
//
//    fun <C> selectObjs(wrapper: Wrapper<T>, mapper: Function<in Any, C>): List<C> {
//        return this.selectObjs<C>(wrapper).asSequence().filterNotNull().map{mapper.apply(it)}.toList()
//    }
//}
