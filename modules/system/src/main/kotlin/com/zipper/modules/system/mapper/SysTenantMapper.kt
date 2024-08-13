package com.zipper.modules.system.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.system.domain.entity.SysTenantEntity
import com.zipper.modules.system.domain.vo.SysTenantVo
import org.apache.ibatis.annotations.Mapper


/**
 * 租户Mapper接口
 *
 * @author Michelle.Chung
 */
@Mapper
interface SysTenantMapper : BaseMapperPlusVo<SysTenantEntity, SysTenantVo>
