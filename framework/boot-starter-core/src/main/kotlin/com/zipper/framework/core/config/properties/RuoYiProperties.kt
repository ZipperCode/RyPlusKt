package com.zipper.framework.core.config.properties

import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 读取项目相关配置
 *
 * @param name 项目名称
 * @param version 项目版本
 * @param copyrightYear 版权年份
 *
 */
@Component
@ConfigurationProperties(prefix = "ruoyi")
class RuoYiProperties {
    var name: String = "若依PlusKt"
    var version: String = "1.0.0"
    var copyrightYear: String = "2024"
}
