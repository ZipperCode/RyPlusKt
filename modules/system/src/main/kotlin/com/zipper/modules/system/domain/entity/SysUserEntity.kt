package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.*
import com.zipper.framework.tanent.core.TenantEntity
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.NoArgsConstructor
import com.zipper.framework.core.constant.UserConstants
import java.util.*

/**
 * 用户对象 sys_user
 *
 * @author Lion Li
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
class SysUserEntity : TenantEntity() {
    /**
     * 用户ID
     */
    @field:TableId(value = "user_id")
    var userId: Long? = null

    /**
     * 部门ID
     */
    var deptId: Long? = null

    /**
     * 用户账号
     */
    var userName: String? = null

    /**
     * 用户昵称
     */
    var nickName: String? = null

    /**
     * 用户类型（sys_user系统用户）
     */
    var userType: String? = null

    /**
     * 用户邮箱
     */
    var email: String? = null

    /**
     * 手机号码
     */
    var phonenumber: String? = null

    /**
     * 用户性别
     */
    var sex: String? = null

    /**
     * 用户头像
     */
    var avatar: Long? = null

    /**
     * 密码
     */
    @field:TableField(
        insertStrategy = FieldStrategy.NOT_EMPTY,
        updateStrategy = FieldStrategy.NOT_EMPTY,
        whereStrategy = FieldStrategy.NOT_EMPTY
    )
    var password: String? = null

    /**
     * 帐号状态（0正常 1停用）
     */
    var status: String? = null

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @field:TableLogic
    var delFlag: String? = null

    /**
     * 最后登录IP
     */
    var loginIp: String? = null

    /**
     * 最后登录时间
     */
    var loginDate: Date? = null

    /**
     * 备注
     */
    var remark: String? = null


    val isSuperAdmin: Boolean
        get() = UserConstants.SUPER_ADMIN_ID == this.userId
}
