package com.zipper.modules.system.domain.bo

import com.zipper.framework.tanent.core.TenantEntity
import com.zipper.modules.system.domain.entity.SysSocialEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.NoArgsConstructor
import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup

/**
 * 社会化关系业务对象 sys_social
 *
 * @author Lion Li
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysSocialEntity::class, reverseConvertGenerate = false)
class SysSocialBo : TenantEntity() {
    /**
     * 主键
     */
    @field:NotNull(message = "主键不能为空", groups = [EditGroup::class])
    var id: Long? = null

    /**
     * 的唯一ID
     */
    @field:NotBlank(message = "的唯一ID不能为空", groups = [AddGroup::class, EditGroup::class])
    var authId: String? = null

    /**
     * 用户来源
     */
    @field:NotBlank(message = "用户来源不能为空", groups = [AddGroup::class, EditGroup::class])
    var source: String? = null

    /**
     * 用户的授权令牌
     */
    @field:NotBlank(message = "用户的授权令牌不能为空", groups = [AddGroup::class, EditGroup::class])
    var accessToken: String? = null

    /**
     * 用户的授权令牌的有效期，部分平台可能没有
     */
    var expireIn = 0

    /**
     * 刷新令牌，部分平台可能没有
     */
    var refreshToken: String? = null

    /**
     * 平台唯一id
     */
    var openId: String? = null

    /**
     * 用户的 ID
     */
    @field:NotBlank(message = "用户的 ID不能为空", groups = [AddGroup::class, EditGroup::class])
    var userId: Long? = null

    /**
     * 平台的授权信息，部分平台可能没有
     */
    var accessCode: String? = null

    /**
     * 用户的 unionid
     */
    var unionId: String? = null

    /**
     * 授予的权限，部分平台可能没有
     */
    var scope: String? = null

    /**
     * 授权的第三方账号
     */
    var userName: String? = null

    /**
     * 授权的第三方昵称
     */
    var nickName: String? = null

    /**
     * 授权的第三方邮箱
     */
    var email: String? = null

    /**
     * 授权的第三方头像地址
     */
    var avatar: String? = null

    /**
     * 个别平台的授权信息，部分平台可能没有
     */
    var tokenType: String? = null

    /**
     * id token，部分平台可能没有
     */
    var idToken: String? = null

    /**
     * 小米平台用户的附带属性，部分平台可能没有
     */
    var macAlgorithm: String? = null

    /**
     * 小米平台用户的附带属性，部分平台可能没有
     */
    var macKey: String? = null

    /**
     * 用户的授权code，部分平台可能没有
     */
    var code: String? = null

    /**
     * Twitter平台用户的附带属性，部分平台可能没有
     */
    var oauthToken: String? = null

    /**
     * Twitter平台用户的附带属性，部分平台可能没有
     */
    var oauthTokenSecret: String? = null
}
