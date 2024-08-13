package com.zipper.framework.social.utils

import cn.hutool.core.util.ObjectUtil
import cn.hutool.extra.spring.SpringUtil
import com.zipper.framework.social.config.properties.SocialProperties
import com.zipper.framework.social.maxkey.AuthMaxKeyRequest
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.exception.AuthException
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.request.*
import com.zipper.framework.core.utils.ktext.forceCast
import java.util.*

/**
 * 认证授权工具类
 *
 * @author thiszhc
 */
object SocialUtils {
    private val STATE_CACHE: AuthRedisStateCache by lazy {
        SpringUtil.getBean(AuthRedisStateCache::class.java)
    }

    @JvmStatic
    @Throws(AuthException::class)
    fun loginAuth(source: String, code: String?, state: String?, socialProperties: SocialProperties): AuthResponse<AuthUser> {
        val authRequest = getAuthRequest(source, socialProperties)
        val callback = AuthCallback()
        callback.code = code
        callback.state = state
        return authRequest.login(callback).forceCast()
    }

    @JvmStatic
    @Throws(AuthException::class)
    fun getAuthRequest(source: String, socialProperties: SocialProperties): AuthRequest {
        val obj = socialProperties.type[source]
        if (ObjectUtil.isNull(obj)) {
            throw AuthException("不支持的第三方登录类型")
        }
        val builder = AuthConfig.builder()
            .clientId(obj!!.clientId)
            .clientSecret(obj.clientSecret)
            .redirectUri(obj.redirectUri)
        return when (source.lowercase(Locale.getDefault())) {
            "dingtalk" -> AuthDingTalkRequest(builder.build(), STATE_CACHE)
            "baidu" -> AuthBaiduRequest(builder.build(), STATE_CACHE)
            "github" -> AuthGithubRequest(builder.build(), STATE_CACHE)
            "gitee" -> AuthGiteeRequest(builder.build(), STATE_CACHE)
            "weibo" -> AuthWeiboRequest(builder.build(), STATE_CACHE)
            "coding" -> AuthCodingRequest(builder.build(), STATE_CACHE)
            "oschina" -> AuthOschinaRequest(builder.build(), STATE_CACHE)
            "alipay_wallet" -> AuthAlipayRequest(builder.build(), socialProperties.type["alipay_wallet"]!!.alipayPublicKey, STATE_CACHE)
            "qq" -> AuthQqRequest(builder.build(), STATE_CACHE)
            "wechat_open" -> AuthWeChatOpenRequest(builder.build(), STATE_CACHE)
            "taobao" -> AuthTaobaoRequest(builder.build(), STATE_CACHE)
            "douyin" -> AuthDouyinRequest(builder.build(), STATE_CACHE)
            "linkedin" -> AuthLinkedinRequest(builder.build(), STATE_CACHE)
            "microsoft" -> AuthMicrosoftRequest(builder.build(), STATE_CACHE)
            "renren" -> AuthRenrenRequest(builder.build(), STATE_CACHE)
            "stack_overflow" -> AuthStackOverflowRequest(builder.stackOverflowKey("").build(), STATE_CACHE)
            "huawei" -> AuthHuaweiRequest(builder.build(), STATE_CACHE)
            "wechat_enterprise" -> AuthWeChatEnterpriseQrcodeRequest(builder.agentId("").build(), STATE_CACHE)
            "gitlab" -> AuthGitlabRequest(builder.build(), STATE_CACHE)
            "wechat_mp" -> AuthWeChatMpRequest(builder.build(), STATE_CACHE)
            "aliyun" -> AuthAliyunRequest(builder.build(), STATE_CACHE)
            "maxkey" -> AuthMaxKeyRequest(builder.build(), STATE_CACHE)
            else -> throw AuthException("未获取到有效的Auth配置")
        }
    }
}

