package com.zipper.modules.system.runner

import com.zipper.framework.core.ext.log
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/**
 * 初始化 system 模块对应业务数据
 *
 * @author Lion Li
 */
@Component
class SystemApplicationRunner : ApplicationRunner {

    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        log.info("初始System成功")
    }
}
