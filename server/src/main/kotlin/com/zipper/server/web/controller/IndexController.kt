package com.zipper.server.web.controller

import cn.dev33.satoken.annotation.SaIgnore
import com.zipper.framework.core.config.properties.RuoYiProperties
import com.zipper.framework.core.utils.StringUtilsExt
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 首页
 *
 * @author Lion Li
 */
@SaIgnore
@RestController
class IndexController(
    @Qualifier("ruoyi-com.zipper.framework.core.config.properties.RuoYiProperties")
    private val ruoyiConfig: RuoYiProperties
) {

    /**
     * 访问首页，提示语
     */
    @GetMapping("/")
    fun index(): String {
        return StringUtilsExt.format(
            "欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。",
            ruoyiConfig.name,
            ruoyiConfig.version
        )
    }
}
