package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysConfigBo
import com.zipper.modules.system.domain.vo.SysConfigVo
import com.zipper.modules.system.service.config.ISysConfigService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.zipper.framework.excel.utils.ExcelUtil.exportExcel

/**
 * 参数配置 信息操作处理
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/system/config")
class SysConfigController(private val configService: ISysConfigService) : BaseController() {
    /**
     * 获取参数配置列表
     */
    @SaCheckPermission("system:config:list")
    @GetMapping("/list")
    fun list(config: SysConfigBo, pageQuery: PageQuery): TableDataInfo<SysConfigVo> {
        return configService.selectPageConfigList(config, pageQuery)
    }

    /**
     * 导出参数配置列表
     */
    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:config:export")
    @PostMapping("/export")
    fun export(config: SysConfigBo?, response: HttpServletResponse?) {
        val list = configService.selectConfigList(config!!)
        exportExcel(list, "参数数据", SysConfigVo::class.java, response!!)
    }

    /**
     * 根据参数编号获取详细信息
     *
     * @param configId 参数ID
     */
    @SaCheckPermission("system:config:query")
    @GetMapping(value = ["/{configId}"])
    fun getInfo(@PathVariable configId: Long?): R<SysConfigVo?> {
        return R.ok(configService.selectConfigById(configId))
    }

    /**
     * 根据参数键名查询参数值
     *
     * @param configKey 参数Key
     */
    @GetMapping(value = ["/configKey/{configKey}"])
    fun getConfigKey(@PathVariable configKey: String?): R<String?> {
        return R.ok("操作成功", configService.selectConfigByKey(configKey))
    }

    /**
     * 新增参数配置
     */
    @SaCheckPermission("system:config:add")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    fun add(@Validated @RequestBody config: SysConfigBo): R<Void> {
        if (!configService.checkConfigKeyUnique(config)) {
            return R.fail("新增参数'" + config.configName + "'失败，参数键名已存在")
        }
        configService.insertConfig(config)
        return R.ok()
    }

    /**
     * 修改参数配置
     */
    @SaCheckPermission("system:config:edit")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    fun edit(@Validated @RequestBody config: SysConfigBo): R<Void> {
        if (!configService.checkConfigKeyUnique(config)) {
            return R.fail("修改参数'" + config.configName + "'失败，参数键名已存在")
        }
        configService.updateConfig(config)
        return R.ok()
    }

    /**
     * 根据参数键名修改参数配置
     */
    @SaCheckPermission("system:config:edit")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping("/updateByKey")
    fun updateByKey(@RequestBody config: SysConfigBo?): R<Void> {
        configService.updateConfig(config!!)
        return R.ok()
    }

    /**
     * 删除参数配置
     *
     * @param configIds 参数ID串
     */
    @SaCheckPermission("system:config:remove")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    fun remove(@PathVariable configIds: Array<Long>): R<Void> {
        configService.deleteConfigByIds(configIds)
        return R.ok()
    }

    /**
     * 刷新参数缓存
     */
    @SaCheckPermission("system:config:remove")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    fun refreshCache(): R<Void> {
        configService.resetConfigCache()
        return R.ok()
    }
}
