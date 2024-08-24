package com.zipper.modules.storage.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.modules.storage.domain.entity.SysFileRecordEntity
import org.apache.ibatis.annotations.Mapper

@Mapper
interface FileRecordMapper : BaseMapperPlus<SysFileRecordEntity> {
}