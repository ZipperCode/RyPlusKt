package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.tanent.core.TenantEntity
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 通知公告表 sys_notice
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice")
class SysNoticeEntity : TenantEntity() {
    /**
     * 公告ID
     */
    @field:TableId(value = "notice_id")
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
}
