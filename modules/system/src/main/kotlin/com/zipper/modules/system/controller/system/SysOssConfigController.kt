package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zipper.framework.core.domain.R
import com.zipper.framework.idempotent.annotation.RepeatSubmit
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysOssConfigBo
import com.zipper.modules.system.domain.vo.SysOssConfigVo
import com.zipper.modules.system.service.oss.ISysOssConfigService
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import com.zipper.framework.core.validate.QueryGroup

/**
 * 对象存储配置
 *
 * @author Lion Li
 * @author 孤舟烟雨
 * @date 2021-08-13
 */
@Validated
@RestController
@RequestMapping("/resource/oss/config")
class SysOssConfigController(
    private val ossConfigService: ISysOssConfigService
) : BaseController() {


    /**
     * 查询对象存储配置列表
     */
    @SaCheckPermission("system:ossConfig:list")
    @GetMapping("/list")
    fun list(
        @Validated(QueryGroup::class) bo: SysOssConfigBo, pageQuery: PageQuery
    ): TableDataInfo<SysOssConfigVo> {
        return ossConfigService.queryPageList(bo, pageQuery)
    }

    /**
     * 获取对象存储配置详细信息
     *
     * @param ossConfigId OSS配置ID
     */
    @SaCheckPermission("system:ossConfig:list")
    @GetMapping("/{ossConfigId}")
    fun getInfo(@PathVariable ossConfigId: @NotNull(message = "主键不能为空") Long?): R<SysOssConfigVo?> {
        return R.ok(ossConfigService.queryById(ossConfigId))
    }

    /**
     * 新增对象存储配置
     */
    @SaCheckPermission("system:ossConfig:add")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(
        @Validated(AddGroup::class) @RequestBody bo: SysOssConfigBo
    ): R<Void> {
        return toAjax(ossConfigService.insertByBo(bo))
    }

    /**
     * 修改对象存储配置
     */
    @SaCheckPermission("system:ossConfig:edit")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(
        @Validated(EditGroup::class) @RequestBody bo: SysOssConfigBo
    ): R<Void> {
        return toAjax(ossConfigService.updateByBo(bo))
    }

    /**
     * 删除对象存储配置
     *
     * @param ossConfigIds OSS配置ID串
     */
    @SaCheckPermission("system:ossConfig:remove")
    @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossConfigIds}")
    fun remove(@PathVariable ossConfigIds: Array<Long>): R<Void> {
        return toAjax(ossConfigService.deleteWithValidByIds(listOf(*ossConfigIds), true))
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:ossConfig:edit")
    @Log(title = "对象存储状态修改", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody bo: SysOssConfigBo): R<Void> {
        return toAjax(ossConfigService.updateOssConfigStatus(bo))
    }
}
