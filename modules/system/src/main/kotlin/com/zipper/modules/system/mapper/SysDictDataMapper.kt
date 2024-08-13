package com.zipper.modules.system.mapper

import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.system.domain.entity.SysDictDataEntity
import com.zipper.modules.system.domain.vo.SysDictDataVo

/**
 * 字典表 数据层
 *
 * @author Lion Li
 */
interface SysDictDataMapper : BaseMapperPlusVo<SysDictDataEntity, SysDictDataVo> {

}

fun SysDictDataMapper.selectDictDataByType(dictType: String?): List<SysDictDataVo> {
    return this.selectVoList(
        MybatisKt.ktQuery<SysDictDataEntity>()
            .eq(SysDictDataEntity::dictType, dictType)
            .orderByAsc(SysDictDataEntity::dictSort)
    )
}

