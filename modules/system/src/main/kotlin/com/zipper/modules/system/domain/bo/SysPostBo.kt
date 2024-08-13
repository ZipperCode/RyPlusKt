package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysPostEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 岗位信息业务对象 sys_post
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysPostEntity::class, reverseConvertGenerate = false)
class SysPostBo : BaseEntity() {
    /**
     * 岗位ID
     */
    var postId: Long? = null

    /**
     * 岗位编码
     */
    @field:NotBlank(message = "岗位编码不能为空")
    @field:Size(
        min = 0,
        max = 64,
        message = "岗位编码长度不能超过{max}个字符"
    )
    var postCode: String? = null

    /**
     * 岗位名称
     */
    @field:NotBlank(message = "岗位名称不能为空")
    @field:Size(
        min = 0,
        max = 50,
        message = "岗位名称长度不能超过{max}个字符"
    )
    var postName: String? = null

    /**
     * 显示顺序
     */
    @field:NotNull(message = "显示顺序不能为空")
    var postSort: Int? = null

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null

    /**
     * 备注
     */
    var remark: String? = null
}
