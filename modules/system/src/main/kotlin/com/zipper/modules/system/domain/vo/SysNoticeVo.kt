package com.zipper.modules.system.domain.vo

import com.zipper.framework.translation.annotation.Translation
import com.zipper.framework.translation.constant.TransConstant
import com.zipper.modules.system.domain.entity.SysNoticeEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 通知公告视图对象 sys_notice
 *
 * @author Michelle.Chung
 */
@Data
@AutoMapper(target = SysNoticeEntity::class)
class SysNoticeVo : Serializable {
    /**
     * 公告ID
     */
    var noticeId: Long? = null

    /**
     * 公告标题
     */
    var noticeTitle: String? = null

    /**
     * 公告类型（1通知 2公告）
     */
    var noticeType: String? = null

    /**
     * 公告内容
     */
    var noticeContent: String? = null

    /**
     * 公告状态（0正常 1关闭）
     */
    var status: String? = null

    /**
     * 备注
     */
    var remark: String? = null

    /**
     * 创建者
     */
    var createBy: Long? = null

    /**
     * 创建人名称
     */
    @field:Translation(type = TransConstant.USER_ID_TO_NAME, mapper = "createBy")
    var createByName: String? = null

    /**
     * 创建时间
     */
    var createTime: Date? = null
}
