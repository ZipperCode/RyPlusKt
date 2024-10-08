package com.zipper.framework.security.config

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.`fun`.SaFunction
import cn.dev33.satoken.interceptor.SaInterceptor
import cn.dev33.satoken.router.SaRouter
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.extra.spring.SpringUtil
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.security.config.properties.SecurityProperties
import com.zipper.framework.security.handler.AllUrlHandler
import lombok.RequiredArgsConstructor
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import com.zipper.framework.core.utils.ServletUtils

/**
 * 权限安全配置
 *
 * @author Lion Li
 */
@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfig(
    private val securityProperties: SecurityProperties
) : WebMvcConfigurer {


    /**
     * 注册sa-token的拦截器
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(SaInterceptor {
            val allUrlHandler = SpringUtil.getBean(AllUrlHandler::class.java)
            // 登录验证 -- 排除多个路径
            SaRouter // 获取所有的
                .match(allUrlHandler.urls) // 对未排除的路径进行检查
                .check(SaFunction {
                    // 检查是否登录 是否有token
                    StpUtil.checkLogin()

                    // 检查 header 与 param 里的 clientid 与 token 里的是否一致
                    val headerCid = ServletUtils.getRequest()?.getHeader(LoginHelper.CLIENT_KEY)
                    val paramCid = ServletUtils.getParameter(LoginHelper.CLIENT_KEY)
                    val clientId = StpUtil.getExtra(LoginHelper.CLIENT_KEY).toString()
                    if (!StringUtils.equalsAny(clientId, headerCid, paramCid)) {
                        // token 无效
                        throw NotLoginException.newInstance(
                            StpUtil.getLoginType(),
                            "-100", "客户端ID与Token不匹配",
                            StpUtil.getTokenValue()
                        )
                    }
                })
        }).addPathPatterns("/**") // 排除不需要拦截的路径
            .excludePathPatterns(*securityProperties!!.excludes)
    }
}
