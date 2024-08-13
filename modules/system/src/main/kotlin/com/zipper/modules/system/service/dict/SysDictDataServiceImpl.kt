package com.zipper.modules.system.service.dict

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.redis.utils.CacheUtils.evict
import com.zipper.modules.system.domain.bo.SysDictDataBo
import com.zipper.modules.system.domain.entity.SysDictDataEntity
import com.zipper.modules.system.domain.vo.SysDictDataVo
import com.zipper.modules.system.mapper.SysDictDataMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Service
import com.zipper.framework.core.constant.CacheNames
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.modules.system.mapper.selectDictDataByType

/**
 * 字典 业务层处理
 *
 * @author Lion Li
 */
@Service
class SysDictDataServiceImpl(
    private val baseMapper: SysDictDataMapper
) : ISysDictDataService {


    override fun selectPageDictDataList(dictData: SysDictDataBo, pageQuery: PageQuery): TableDataInfo<SysDictDataVo> {
        val lqw = buildQueryWrapper(dictData)
        val page = baseMapper.selectVoPage<Page<SysDictDataVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(page)
    }

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    override fun selectDictDataList(dictData: SysDictDataBo): List<SysDictDataVo> {
        val lqw = buildQueryWrapper(dictData)
        return baseMapper.selectVoList(lqw)
    }

    private fun buildQueryWrapper(bo: SysDictDataBo): KtQueryWrapper<SysDictDataEntity> {
        val lqw = MybatisKt.ktQuery<SysDictDataEntity>()
        lqw.eq(bo.dictSort != null, SysDictDataEntity::dictSort, bo.dictSort)
        lqw.like(StringUtils.isNotBlank(bo.dictLabel), SysDictDataEntity::dictLabel, bo.dictLabel)
        lqw.eq(StringUtils.isNotBlank(bo.dictType), SysDictDataEntity::dictType, bo.dictType)
        lqw.orderByAsc(SysDictDataEntity::dictSort)
        return lqw
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    override fun selectDictLabel(dictType: String?, dictValue: String?): String? {
        return baseMapper.selectOne(
            MybatisKt.ktQuery<SysDictDataEntity>()
                .select(SysDictDataEntity::dictLabel)
                .eq(SysDictDataEntity::dictType, dictType)
                .eq(SysDictDataEntity::dictValue, dictValue)
        )
            .dictLabel
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    override fun selectDictDataById(dictCode: Long?): SysDictDataVo? {
        return baseMapper.selectVoById(dictCode)
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    override fun deleteDictDataByIds(dictCodes: Array<Long>) {
        for (dictCode in dictCodes) {
            val data = baseMapper.selectById(dictCode)
            baseMapper.deleteById(dictCode)
            data.dictType?.let { evict(CacheNames.SYS_DICT, it) }
        }
    }

    /**
     * 新增保存字典数据信息
     *
     * @param bo 字典数据信息
     * @return 结果
     */
    @CachePut(cacheNames = [CacheNames.SYS_DICT], key = "#bo.dictType")
    override fun insertDictData(bo: SysDictDataBo): List<SysDictDataVo> {
        val data = convert(bo, SysDictDataEntity::class.java)
        val row = baseMapper.insert(data)
        if (row > 0) {
            return baseMapper.selectDictDataByType(data.dictType)
        }
        throw ServiceException("操作失败")
    }

    /**
     * 修改保存字典数据信息
     *
     * @param bo 字典数据信息
     * @return 结果
     */
    @CachePut(cacheNames = [CacheNames.SYS_DICT], key = "#bo.dictType")
    override fun updateDictData(bo: SysDictDataBo): List<SysDictDataVo> {
        val data = convert(bo, SysDictDataEntity::class.java)
        val row = baseMapper.updateById(data)
        if (row > 0) {
            return baseMapper.selectDictDataByType(data.dictType)
        }
        throw ServiceException("操作失败")
    }

}
