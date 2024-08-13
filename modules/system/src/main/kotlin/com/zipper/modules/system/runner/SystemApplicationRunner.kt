package com.zipper.modules.system.runner

import com.zipper.modules.system.service.oss.ISysOssConfigService
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import com.zipper.framework.core.ext.log

/**
 * 初始化 system 模块对应业务数据
 *
 * @author Lion Li
 */
@Component
class SystemApplicationRunner(
    private val ossConfigService: ISysOssConfigService
) : ApplicationRunner {


    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        ossConfigService.init()
        log.info("初始化OSS配置成功")
    }
}
