package com.zipper.modules.auth.domain.param

import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import com.zipper.framework.mybatis.core.domain.BaseMixinVo
import com.zipper.framework.tanent.core.TenantMixin
import com.zipper.modules.auth.domain.SysSocialMixin
import com.zipper.modules.auth.domain.entity.SysSocialEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysSocialEntity::class, reverseConvertGenerate = false)
class SysSocialSaveParam : BaseMixinVo(), SysSocialMixin, TenantMixin {

    /**
     * 主键
     */
    @field:NotNull(message = "主键不能为空", groups = [EditGroup::class])
    override var id: Long? = null

    override var tenantId: String? = null

    /**
     * 的唯一ID
     */
    @field:NotBlank(message = "的唯一ID不能为空", groups = [AddGroup::class, EditGroup::class])
    override var authId: String? = null

    /**
     * 用户来源
     */
    @field:NotBlank(message = "用户来源不能为空", groups = [AddGroup::class, EditGroup::class])
    override var source: String? = null

    /**
     * 用户的授权令牌
     */
    @field:NotBlank(message = "用户的授权令牌不能为空", groups = [AddGroup::class, EditGroup::class])
    override var accessToken: String? = null

    /**
     * 用户的授权令牌的有效期，部分平台可能没有
     */
    override var expireIn = 0

    /**
     * 刷新令牌，部分平台可能没有
     */
    override var refreshToken: String? = null

    /**
     * 平台唯一id
     */
    override var openId: String? = null

    /**
     * 用户的 ID
     */
    @field:NotBlank(message = "用户的 ID不能为空", groups = [AddGroup::class, EditGroup::class])
    override var userId: Long? = null

    /**
     * 平台的授权信息，部分平台可能没有
     */
    override var accessCode: String? = null

    /**
     * 用户的 unionid
     */
    override var unionId: String? = null

    /**
     * 授予的权限，部分平台可能没有
     */
    override var scope: String? = null

    /**
     * 授权的第三方账号
     */
    override var userName: String? = null

    /**
     * 授权的第三方昵称
     */
    override var nickName: String? = null

    /**
     * 授权的第三方邮箱
     */
    override var email: String? = null

    /**
     * 授权的第三方头像地址
     */
    override var avatar: String? = null

    /**
     * 个别平台的授权信息，部分平台可能没有
     */
    override var tokenType: String? = null

    /**
     * id token，部分平台可能没有
     */
    override var idToken: String? = null

    /**
     * 小米平台用户的附带属性，部分平台可能没有
     */
    override var macAlgorithm: String? = null

    /**
     * 小米平台用户的附带属性，部分平台可能没有
     */
    override var macKey: String? = null

    /**
     * 用户的授权code，部分平台可能没有
     */
    override var code: String? = null

    /**
     * Twitter平台用户的附带属性，部分平台可能没有
     */
    override var oauthToken: String? = null

    /**
     * Twitter平台用户的附带属性，部分平台可能没有
     */
    override var oauthTokenSecret: String? = null
}