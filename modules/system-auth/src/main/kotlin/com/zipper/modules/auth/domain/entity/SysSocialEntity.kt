package com.zipper.modules.auth.domain.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.mybatis.core.domain.BaseMixinEntity
import com.zipper.framework.tanent.core.TenantMixin
import com.zipper.modules.auth.domain.SysSocialMixin
import lombok.Data
import lombok.EqualsAndHashCode
import java.io.Serial

/**
 * 社会化关系对象 sys_social
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_social")
class SysSocialEntity : BaseMixinEntity(), SysSocialMixin {
    /**
     * 主键
     */
    @field:TableId(value = "id")
    override var id: Long? = null

    override var tenantId: String? = null

    /**
     * 用户ID
     */
    override var userId: Long? = null

    /**
     * 的唯一ID
     */
    override var authId: String? = null

    /**
     * 用户来源
     */
    override var source: String? = null

    /**
     * 用户的授权令牌
     */
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
     * 用户的 open id
     */
    override var openId: String? = null

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


    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
