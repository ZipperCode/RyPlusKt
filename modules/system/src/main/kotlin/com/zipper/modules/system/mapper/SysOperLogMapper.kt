package com.zipper.modules.system.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.system.domain.entity.SysOperLogEntity
import com.zipper.modules.system.domain.vo.SysOperLogVo


/**
 * 操作日志 数据层
 *
 * @author Lion Li
 */
interface SysOperLogMapper : BaseMapperPlusVo<SysOperLogEntity, SysOperLogVo>
