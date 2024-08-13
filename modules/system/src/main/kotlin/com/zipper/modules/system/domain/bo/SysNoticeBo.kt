package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysNoticeEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import lombok.Data
import lombok.EqualsAndHashCode
import com.zipper.framework.core.xss.Xss

/**
 * 通知公告业务对象 sys_notice
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysNoticeEntity::class, reverseConvertGenerate = false)
class SysNoticeBo : BaseEntity() {
    /**
     * 公告ID
     */
    var noticeId: Long? = null

    /**
     * 公告标题
     */
    @field:Xss(message = "公告标题不能包含脚本字符")
    @field:NotBlank(message = "公告标题不能为空")
    @field:Size(
        min = 0,
        max = 50,
        message = "公告标题不能超过{max}个字符"
    )
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
     * 创建人名称
     */
    var createByName: String? = null
}
