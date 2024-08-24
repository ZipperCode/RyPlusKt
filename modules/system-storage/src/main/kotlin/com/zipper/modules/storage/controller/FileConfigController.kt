package com.zipper.modules.storage.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zipper.framework.core.domain.R
import com.zipper.framework.core.validate.EditGroup
import com.zipper.framework.idempotent.annotation.RepeatSubmit
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.storage.domain.param.FileConfigPageParam
import com.zipper.modules.storage.domain.param.FileConfigSaveParam
import com.zipper.modules.storage.domain.vo.FileConfigVo
import com.zipper.modules.storage.service.FileConfigService
import jakarta.annotation.Nonnull
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@io.swagger.v3.oas.annotations.tags.Tag(name = "管理后台 - 文件配置")
@RestController
@RequestMapping("/store/file/config")
@Validated
class FileConfigController(
    private val fileConfigService: FileConfigService
) : BaseController() {
    @SaCheckPermission("store:fileConfig:add")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Valid @RequestBody param: FileConfigSaveParam): R<Void> {
        return toAjax(fileConfigService.createConfig(param))
    }

    @SaCheckPermission("store:fileConfig:edit")
    @Log(title = "编辑存储配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated(EditGroup::class) @RequestBody param: FileConfigSaveParam): R<Void> {
        return toAjax(fileConfigService.updateConfig(param))
    }

    @SaCheckPermission("store:fileConfig:delete")
    @Log(title = "删除存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    fun delete(@PathVariable configIds: Array<Long>): R<Void> {
        return toAjax(fileConfigService.deleteConfig(configIds))
    }

    @SaCheckPermission("system:fileConfig:edit")
    @Log(title = "切换主配置", businessType = BusinessType.UPDATE)
    @PutMapping("/updateMaster")
    fun updateMaster(configId: Long): R<Void> {
        fileConfigService.updateMaster(configId)
        return success()
    }

    @SaCheckPermission("system:fileConfig:list")
    @GetMapping("/{configId}")
    fun getInfo(@PathVariable configId: @NotNull(message = "key不能为空") Long): R<FileConfigVo?> {
        return successOrNull(fileConfigService.getConfig(configId))
    }


    @SaCheckPermission("store:ossConfig:list")
    @GetMapping("/list")
    fun list(@Valid param: FileConfigPageParam): TableDataInfo<FileConfigVo> {
        return fileConfigService.pageList(param)
    }
}