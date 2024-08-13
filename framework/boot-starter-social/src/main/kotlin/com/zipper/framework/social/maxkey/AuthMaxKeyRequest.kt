package com.zipper.framework.social.maxkey

import cn.hutool.extra.spring.SpringUtil
import com.zipper.framework.json.utils.JsonUtils
import me.zhyd.oauth.cache.AuthStateCache
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.exception.AuthException
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthToken
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.request.AuthDefaultRequest

/**
 * @author 长春叭哥 2023年03月26日
 */
class AuthMaxKeyRequest : AuthDefaultRequest {
    /**
     * 设定归属域
     */
    constructor(config: AuthConfig?) : super(config, AuthMaxKeySource.MAXKEY)

    constructor(config: AuthConfig?, authStateCache: AuthStateCache?) : super(config, AuthMaxKeySource.MAXKEY, authStateCache)

    override fun getAccessToken(authCallback: AuthCallback): AuthToken {
        val body = doPostAuthorizationCode(authCallback.code)
        val dict = JsonUtils.parseMap(body)!!
        // oauth/token 验证异常
        if (dict.containsKey("error")) {
            throw AuthException(dict.getStr("error_description"))
        }
        // user 验证异常
        if (dict.containsKey("message")) {
            throw AuthException(dict.getStr("message"))
        }
        return AuthToken.builder()
            .accessToken(dict.getStr("access_token"))
            .refreshToken(dict.getStr("refresh_token"))
            .idToken(dict.getStr("id_token"))
            .tokenType(dict.getStr("token_type"))
            .scope(dict.getStr("scope"))
            .build()
    }

    override fun getUserInfo(authToken: AuthToken): AuthUser {
        val body = doGetUserInfo(authToken)
        val dict = JsonUtils.parseMap(body)!!
        // oauth/token 验证异常
        if (dict.containsKey("error")) {
            throw AuthException(dict.getStr("error_description"))
        }
        // user 验证异常
        if (dict.containsKey("message")) {
            throw AuthException(dict.getStr("message"))
        }
        return AuthUser.builder()
            .uuid(dict.getStr("userId"))
            .username(dict.getStr("username"))
            .nickname(dict.getStr("displayName"))
            .avatar(dict.getStr("avatar_url"))
            .blog(dict.getStr("web_url"))
            .company(dict.getStr("organization"))
            .location(dict.getStr("location"))
            .email(dict.getStr("email"))
            .remark(dict.getStr("bio"))
            .token(authToken)
            .source(source.toString())
            .build()
    }

    companion object {
        val SERVER_URL: String by lazy {
            SpringUtil.getProperty("justauth.type.maxkey.server-url")
        }
    }
}
