package com.zipper.modules.auth.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.auth.domain.entity.SysClientEntity
import com.zipper.modules.auth.domain.vo.SysClientVo
import org.apache.ibatis.annotations.Mapper


/**
 * 授权管理Mapper接口
 *
 */
@Mapper
interface SysClientMapper : BaseMapperPlusVo<SysClientEntity, SysClientVo>
