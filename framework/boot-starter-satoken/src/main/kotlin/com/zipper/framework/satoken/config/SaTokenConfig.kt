package com.zipper.framework.satoken.config

import cn.dev33.satoken.dao.SaTokenDao
import cn.dev33.satoken.jwt.StpLogicJwtForSimple
import cn.dev33.satoken.stp.StpInterface
import cn.dev33.satoken.stp.StpLogic
import com.zipper.framework.satoken.core.dao.PlusSaTokenDao
import com.zipper.framework.satoken.core.service.SaPermissionImpl
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import com.zipper.framework.core.factory.YmlPropertySourceFactory

/**
 * sa-token 配置
 *
 * @author Lion Li
 */
@AutoConfiguration
@PropertySource(value = ["classpath:common-satoken.yml"], factory = YmlPropertySourceFactory::class)
class SaTokenConfig {
    @get:Bean
    val stpLogicJwt: StpLogic
        get() =// Sa-Token 整合 jwt (简单模式)
            StpLogicJwtForSimple()

    /**
     * 权限接口实现(使用bean注入方便用户替换)
     */
    @Bean
    fun stpInterface(): StpInterface {
        return SaPermissionImpl()
    }

    /**
     * 自定义dao层存储
     */
    @Bean
    fun saTokenDao(): SaTokenDao {
        return PlusSaTokenDao()
    }
}
