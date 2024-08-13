package com.zipper.server

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup

/**
 * 启动程序
 *
 * @author Lion Li
 */
@SpringBootApplication(scanBasePackages = ["com.zipper"])
@MapperScan("com.zipper.modules.**.mapper")
class DromaraApplication

fun main(args: Array<String>) {
    val application = SpringApplication(DromaraApplication::class.java)
    application.applicationStartup = BufferingApplicationStartup(2048)
    val ioc = application.run(*args)
    ioc.beanDefinitionNames.forEachIndexed { index, value ->
        println("【${index}】：【${value}】")
    }
    println("(♥◠‿◠)ﾉﾞ  RuoYi-Vue-Plus启动成功   ლ(´ڡ`ლ)ﾞ")
}