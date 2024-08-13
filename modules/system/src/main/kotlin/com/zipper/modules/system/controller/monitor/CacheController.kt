package com.zipper.modules.system.controller.monitor

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zipper.framework.core.domain.R
import com.zipper.modules.system.domain.vo.CacheListInfoVo
import jakarta.annotation.Resource
import org.apache.commons.lang3.StringUtils
import org.redisson.spring.data.connection.RedissonConnectionFactory
import org.springframework.context.annotation.Lazy
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.function.Consumer

/**
 * 缓存监控
 *
 * @author Lion Li
 */
@RestController
@RequestMapping("/monitor/cache")
class CacheController() {

    @Lazy
    @Resource
    private lateinit var connectionFactory: RedissonConnectionFactory
    /**
     * 获取缓存监控列表
     */
    @Throws(Exception::class)
    @GetMapping
    @SaCheckPermission("monitor:cache:list")
    fun getInfo(): R<CacheListInfoVo> {
        val connection = connectionFactory.connection
        val commandStats = connection.commands().info("commandstats")

        val pieList: MutableList<Map<String, String>> = ArrayList()
        commandStats?.stringPropertyNames()?.forEach(Consumer { key: String? ->
            val data: MutableMap<String, String> = HashMap(2)
            val property = commandStats.getProperty(key)
            data["name"] = StringUtils.removeStart(key, "cmdstat_")
            data["value"] = StringUtils.substringBetween(property, "calls=", ",usec")
            pieList.add(data)
        })

        val infoVo = CacheListInfoVo()
        infoVo.info = connection.commands().info()
        infoVo.dbSize = connection.commands().dbSize()
        infoVo.commandStats = pieList
        return R.ok(infoVo)
    }
}
