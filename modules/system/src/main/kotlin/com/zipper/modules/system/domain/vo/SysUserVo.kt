package com.zipper.modules.system.domain.vo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.zipper.framework.sensitive.annotation.Sensitive
import com.zipper.framework.sensitive.core.SensitiveStrategy
import com.zipper.framework.translation.annotation.Translation
import com.zipper.framework.translation.constant.TransConstant
import com.zipper.modules.system.domain.entity.SysUserEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 用户信息视图对象 sys_user
 *
 * @author Michelle.Chung
 */
@Data
@AutoMapper(target = SysUserEntity::class)
class SysUserVo : Serializable {
    /**
     * 用户ID
     */
    var userId: Long = 0

    /**
     * 租户ID
     */
    var tenantId: String? = null

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
    var userType: String = ""

    /**
     * 用户邮箱
     */
    @Sensitive(strategy = SensitiveStrategy.EMAIL)
    var email: String? = null

    /**
     * 手机号码
     */
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    var phonenumber: String? = null

    /**
     * 用户性别（0男 1女 2未知）
     */
    var sex: String? = null

    /**
     * 头像地址
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL)
    var avatar: Long? = null

    /**
     * 密码
     */
    @JsonIgnore
    @JsonProperty
    var password: String? = null

    /**
     * 帐号状态（0正常 1停用）
     */
    var status: String? = null

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

    /**
     * 创建时间
     */
    var createTime: Date? = null

    /**
     * 部门对象
     */
    var dept: SysDeptVo? = null

    /**
     * 角色对象
     */
    var roles: List<SysRoleVo>? = null

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
}
