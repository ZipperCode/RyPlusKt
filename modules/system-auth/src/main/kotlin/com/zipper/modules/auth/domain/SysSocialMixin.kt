package com.zipper.modules.auth.domain

import com.baomidou.mybatisplus.annotation.TableId
import com.zipper.framework.mybatis.core.domain.CreatorMixin
import com.zipper.framework.mybatis.core.domain.UpdaterMixin
import com.zipper.framework.tanent.core.TenantMixin
import java.io.Serializable

interface SysSocialMixin : CreatorMixin, UpdaterMixin, TenantMixin, Serializable {

    /**
     * 主键
     */
    var id: Long?

    /**
     * 用户ID
     */
    var userId: Long?

    /**
     * 的唯一ID
     */
    var authId: String?

    /**
     * 用户来源
     */
    var source: String?

    /**
     * 用户的授权令牌
     */
    var accessToken: String?

    /**
     * 用户的授权令牌的有效期，部分平台可能没有
     */
    var expireIn: Int

    /**
     * 刷新令牌，部分平台可能没有
     */
    var refreshToken: String?

    /**
     * 用户的 open id
     */
    var openId: String?

    /**
     * 授权的第三方账号
     */
    var userName: String?

    /**
     * 授权的第三方昵称
     */
    var nickName: String?

    /**
     * 授权的第三方邮箱
     */
    var email: String?

    /**
     * 授权的第三方头像地址
     */
    var avatar: String?

    /**
     * 平台的授权信息，部分平台可能没有
     */
    var accessCode: String?

    /**
     * 用户的 unionid
     */
    var unionId: String?

    /**
     * 授予的权限，部分平台可能没有
     */
    var scope: String?

    /**
     * 个别平台的授权信息，部分平台可能没有
     */
    var tokenType: String?

    /**
     * id token，部分平台可能没有
     */
    var idToken: String?

    /**
     * 小米平台用户的附带属性，部分平台可能没有
     */
    var macAlgorithm: String?

    /**
     * 小米平台用户的附带属性，部分平台可能没有
     */
    var macKey: String?

    /**
     * 用户的授权code，部分平台可能没有
     */
    var code: String?

    /**
     * Twitter平台用户的附带属性，部分平台可能没有
     */
    var oauthToken: String?

    /**
     * Twitter平台用户的附带属性，部分平台可能没有
     */
    var oauthTokenSecret: String?
}