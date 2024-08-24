package com.zipper.modules.storage.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.core.utils.MapstructUtils
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.core.betweenIfPresent
import com.zipper.framework.mybatis.core.likeIfPresent
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.storage.domain.bo.FileCreateBo
import com.zipper.modules.storage.domain.entity.SysFileRecordEntity
import com.zipper.modules.storage.domain.param.FileRecordPageParam
import com.zipper.modules.storage.domain.vo.FileRecordVo
import com.zipper.modules.storage.mapper.FileRecordMapper
import org.springframework.stereotype.Service

@Service
class FileRecordServiceImpl(
    private val fileRecordMapper: FileRecordMapper
) : FileRecordService {
    override fun create(fileCreateBo: FileCreateBo): FileRecordVo {
        val entity = SysFileRecordEntity()
        MapstructUtils.convert(fileCreateBo, entity)
        fileRecordMapper.insert(entity)
        return MapstructUtils.convert(entity, FileRecordVo::class.java)
    }

    override fun queryByIds(recordIds: Array<Long>): List<SysFileRecordEntity> {
        return fileRecordMapper.selectList(
            MybatisKt.ktQuery<SysFileRecordEntity>()
                .`in`(SysFileRecordEntity::id, recordIds)
        )
    }

    override fun delete(recordIds: Array<Long>): List<SysFileRecordEntity> {
        val deleteRecordList = fileRecordMapper.selectList(
            MybatisKt.ktQuery<SysFileRecordEntity>()
                .`in`(SysFileRecordEntity::id, *recordIds)
        )
        fileRecordMapper.deleteBatchIds(recordIds.toList())
        return deleteRecordList
    }

    override fun pageList(param: FileRecordPageParam): TableDataInfo<FileRecordVo> {
        val queryPage = param.build<SysFileRecordEntity>()
        val pageList = fileRecordMapper.selectList(
            queryPage, MybatisKt.ktQuery<SysFileRecordEntity>()
                .likeIfPresent(SysFileRecordEntity::path, param.path)
                .likeIfPresent(SysFileRecordEntity::mimeType, param.type)
                .betweenIfPresent(SysFileRecordEntity::createTime, param.createTime)
                .orderByDesc(SysFileRecordEntity::id)
        )
        val pageResult = Page<FileRecordVo>(queryPage.current, queryPage.size, queryPage.total)
        pageResult.setRecords(MapstructUtils.convertWithClass(pageList, FileRecordVo::class.java))
        return TableDataInfo.build(pageResult)
    }
}