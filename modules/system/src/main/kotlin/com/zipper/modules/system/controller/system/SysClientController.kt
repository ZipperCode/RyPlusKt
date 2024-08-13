package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zipper.framework.core.domain.R
import com.zipper.framework.idempotent.annotation.RepeatSubmit
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysClientBo
import com.zipper.modules.system.domain.vo.SysClientVo
import com.zipper.modules.system.service.client.ISysClientService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import org.zipper.framework.excel.utils.ExcelUtil

/**
 * 客户端管理
 *
 * @author Michelle.Chung
 * @date 2023-06-18
 */
@Validated
@RestController
@RequestMapping("/system/client")
class SysClientController(private val sysClientService: ISysClientService) : BaseController() {
    /**
     * 查询客户端管理列表
     */
    @SaCheckPermission("system:client:list")
    @GetMapping("/list")
    fun list(bo: SysClientBo?, pageQuery: PageQuery?): TableDataInfo<SysClientVo> {
        return sysClientService.queryPageList(bo!!, pageQuery!!)
    }

    /**
     * 导出客户端管理列表
     */
    @SaCheckPermission("system:client:export")
    @Log(title = "客户端管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    fun export(bo: SysClientBo, response: HttpServletResponse) {
        val list = sysClientService.queryList(bo)
        ExcelUtil.exportExcel(list, "客户端管理", SysClientVo::class.java, response)
    }

    /**
     * 获取客户端管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:client:query")
    @GetMapping("/{id}")
    fun getInfo(@PathVariable id: @NotNull(message = "主键不能为空") Long?): R<SysClientVo?> {
        return R.ok(sysClientService.queryById(id))
    }

    /**
     * 新增客户端管理
     */
    @SaCheckPermission("system:client:add")
    @Log(title = "客户端管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated(AddGroup::class) @RequestBody bo: SysClientBo): R<Void> {
        return toAjax(sysClientService.insertByBo(bo))
    }

    /**
     * 修改客户端管理
     */
    @SaCheckPermission("system:client:edit")
    @Log(title = "客户端管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated(EditGroup::class) @RequestBody bo: SysClientBo): R<Void> {
        return toAjax(sysClientService.updateByBo(bo))
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:client:edit")
    @Log(title = "客户端管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody bo: SysClientBo): R<Void> {
        return toAjax(sysClientService.updateUserStatus(bo.id!!, bo.status!!))
    }

    /**
     * 删除客户端管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:client:remove")
    @Log(title = "客户端管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    fun remove(@PathVariable ids: Array<Long>): R<Void> {
        return toAjax(sysClientService.deleteWithValidByIds(listOf(*ids), true))
    }
}
