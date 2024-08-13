package com.zipper.framework.doc.config.properties

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.tags.Tag
import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * swagger 配置属性
 *
 * @author Lion Li
 */
@ConfigurationProperties(prefix = "springdoc")
class SpringDocProperties {
    /**
     * 文档基本信息
     */
    @NestedConfigurationProperty
    val info = InfoProperties()

    /**
     * 扩展文档地址
     */
    @NestedConfigurationProperty
    var externalDocs: ExternalDocumentation = ExternalDocumentation()

    /**
     * 标签
     */
    var tags: List<Tag> = listOf()

    /**
     * 路径
     */
    @NestedConfigurationProperty
    var paths: Paths = Paths()

    /**
     * 组件
     */
    @NestedConfigurationProperty
    var components: Components = Components()

    /**
     *
     *
     * 文档的基础属性信息
     *
     *
     * @see io.swagger.v3.oas.models.info.Info
     *
     * 为了 springboot 自动生产配置提示信息，所以这里复制一个类出来
     */
    @Data
    class InfoProperties {
        /**
         * 标题
         */
        var title: String = ""

        /**
         * 描述
         */
        var description: String = ""

        /**
         * 联系人信息
         */
        @NestedConfigurationProperty
        var contact: Contact = Contact()

        /**
         * 许可证
         */
        @NestedConfigurationProperty
        var license: License = License()

        /**
         * 版本
         */
        var version: String = ""
    }
}
