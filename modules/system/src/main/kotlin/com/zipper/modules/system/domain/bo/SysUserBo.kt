package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysUserEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.NoArgsConstructor
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.xss.Xss

/**
 * 用户信息业务对象 sys_user
 *
 * @author Michelle.Chung
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysUserEntity::class, reverseConvertGenerate = false)
class SysUserBo : BaseEntity() {
    /**
     * 用户ID
     */
    var userId: Long? = null

    /**
     * 部门ID
     */
    var deptId: Long? = null

    /**
     * 用户账号
     */
    @field:Xss(message = "用户账号不能包含脚本字符")
    @field:NotBlank(message = "用户账号不能为空")
    @field:Size(
        min = 0,
        max = 30,
        message = "用户账号长度不能超过{max}个字符"
    )
    var userName: String? = null

    /**
     * 用户昵称
     */
    @field:Xss(message = "用户昵称不能包含脚本字符")
    @field:NotBlank(message = "用户昵称不能为空")
    @field:Size(
        min = 0,
        max = 30,
        message = "用户昵称长度不能超过{max}个字符"
    )
    var nickName: String? = null

    /**
     * 用户类型（sys_user系统用户）
     */
    var userType: String? = null

    /**
     * 用户邮箱
     */
    @field:Email(message = "邮箱格式不正确")
    @field:Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    var email: String? = null

    /**
     * 手机号码
     */
    var phonenumber: String? = null

    /**
     * 用户性别（0男 1女 2未知）
     */
    var sex: String? = null

    /**
     * 密码
     */
    var password: String? = null

    /**
     * 帐号状态（0正常 1停用）
     */
    var status: String? = null

    /**
     * 备注
     */
    var remark: String? = null

    /**
     * 角色组
     */
    var roleIds: Array<Long> = emptyArray()

    /**
     * 岗位组
     */
    var postIds: Array<Long> = emptyArray()

    /**
     * 数据权限 当前角色ID
     */
    var roleId: Long? = null

    val isSuperAdmin: Boolean
        get() = UserConstants.SUPER_ADMIN_ID.equals(this.userId)
}
