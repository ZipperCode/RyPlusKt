package com.zipper.modules.system.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.system.domain.entity.SysNoticeEntity
import com.zipper.modules.system.domain.vo.SysNoticeVo


/**
 * 通知公告表 数据层
 *
 * @author Lion Li
 */
interface SysNoticeMapper : BaseMapperPlusVo<SysNoticeEntity, SysNoticeVo>
