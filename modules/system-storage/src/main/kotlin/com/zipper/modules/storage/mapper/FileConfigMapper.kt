package com.zipper.modules.storage.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.modules.storage.domain.entity.SysFileConfigEntity
import org.apache.ibatis.annotations.Mapper

@Mapper
interface FileConfigMapper : BaseMapperPlus<SysFileConfigEntity> {

}