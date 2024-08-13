package com.zipper.modules.system.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.system.domain.entity.SysClientEntity
import com.zipper.modules.system.domain.vo.SysClientVo
import org.apache.ibatis.annotations.Mapper


/**
 * 授权管理Mapper接口
 *
 * @author Michelle.Chung
 * @date 2023-05-15
 */
@Mapper
interface SysClientMapper : BaseMapperPlusVo<SysClientEntity, SysClientVo>
