package com.zipper.modules.tenant.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.tenant.domain.entity.SysTenantEntity
import com.zipper.modules.tenant.domain.vo.SysTenantVo
import org.apache.ibatis.annotations.Mapper


/**
 * 租户Mapper接口
 *
 * @author Michelle.Chung
 */
@Mapper
interface SysTenantMapper : BaseMapperPlusVo<SysTenantEntity, SysTenantVo>
