package com.zipper.modules.system.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.system.domain.entity.SysLoginLogEntity
import com.zipper.modules.system.domain.vo.SysLoginLogVo


/**
 * 系统访问日志情况信息 数据层
 *
 * @author Lion Li
 */
interface SysLoginLogMapper : BaseMapperPlusVo<SysLoginLogEntity, SysLoginLogVo>
