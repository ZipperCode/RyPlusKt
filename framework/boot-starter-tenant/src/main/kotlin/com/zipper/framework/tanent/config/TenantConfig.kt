package com.zipper.framework.tanent.config

import cn.dev33.satoken.dao.SaTokenDao
import cn.hutool.core.util.ObjectUtil
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor
import com.zipper.framework.core.ext.log
import com.zipper.framework.mybatis.config.MybatisPlusConfig
import com.zipper.framework.redis.config.RedisConfig
import com.zipper.framework.redis.config.properties.RedissonProperties
import com.zipper.framework.tanent.config.properties.TenantProperties
import com.zipper.framework.tanent.core.TenantSaTokenDao
import com.zipper.framework.tanent.handle.PlusTenantLineHandler
import com.zipper.framework.tanent.handle.TenantKeyPrefixHandler
import com.zipper.framework.tanent.manager.TenantSpringCacheManager
import org.redisson.config.ClusterServersConfig
import org.redisson.config.Config
import org.redisson.config.SingleServerConfig
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import com.zipper.framework.core.utils.reflect.ReflectUtils

/**
 * 租户配置类
 *
 * @author Lion Li
 */
@EnableConfigurationProperties(TenantProperties::class)
@AutoConfiguration(after = [RedisConfig::class, MybatisPlusConfig::class])
@ConditionalOnProperty(value = ["tenant.enable"], havingValue = "true")
class TenantConfig {
    /**
     * 初始化租户配置
     */
    @Bean
    fun tenantInit(
        mybatisPlusInterceptor: MybatisPlusInterceptor,
        tenantProperties: TenantProperties?
    ): Boolean {
        val interceptors: MutableList<InnerInterceptor> = ArrayList()
        // 多租户插件 必须放到第一位
        interceptors.add(tenantLineInnerInterceptor(tenantProperties))
        interceptors.addAll(mybatisPlusInterceptor.interceptors)
        mybatisPlusInterceptor.interceptors = interceptors
        return true
    }

    /**
     * 多租户插件
     */
    fun tenantLineInnerInterceptor(tenantProperties: TenantProperties?): TenantLineInnerInterceptor {
        return TenantLineInnerInterceptor(PlusTenantLineHandler(tenantProperties!!))
    }

    @Bean
    fun tenantRedissonCustomizer(redissonProperties: RedissonProperties): RedissonAutoConfigurationCustomizer {
        return RedissonAutoConfigurationCustomizer { config: Config ->
            val nameMapper = TenantKeyPrefixHandler(redissonProperties.keyPrefix)
            val singleServerConfig = ReflectUtils.invokeGetter<SingleServerConfig>(config, "singleServerConfig")
            if (ObjectUtil.isNotNull(singleServerConfig)) {
                // 使用单机模式
                // 设置多租户 redis key前缀
                singleServerConfig!!.setNameMapper(nameMapper)
                ReflectUtils.invokeSetter(config, "singleServerConfig", singleServerConfig)
            }

            val clusterServersConfig = ReflectUtils.invokeGetter<ClusterServersConfig>(config, "clusterServersConfig")
            // 集群配置方式 参考下方注释
            if (ObjectUtil.isNotNull(clusterServersConfig)) {
                // 设置多租户 redis key前缀
                clusterServersConfig!!.setNameMapper(nameMapper)
                ReflectUtils.invokeSetter(config, "clusterServersConfig", clusterServersConfig)
            }
        }
    }

    /**
     * 多租户缓存管理器
     */
    @Primary
    @Bean
    fun tenantCacheManager(): CacheManager {
        return TenantSpringCacheManager()
    }

    /**
     * 多租户鉴权dao实现
     */
    @Primary
    @Bean
    fun tenantSaTokenDao(): SaTokenDao {
        return TenantSaTokenDao()
    }
}
