package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaCheckRole
import com.zipper.framework.core.domain.R
import com.zipper.framework.idempotent.annotation.RepeatSubmit
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysTenantPackageBo
import com.zipper.modules.system.domain.vo.SysTenantPackageVo
import com.zipper.modules.system.service.tenant.ISysTenantPackageService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import org.zipper.framework.excel.utils.ExcelUtil.exportExcel

/**
 * 租户套餐管理
 *
 * @author Michelle.Chung
 */
@Validated
@RestController
@RequestMapping("/system/tenant/package")
class SysTenantPackageController(private val tenantPackageService: ISysTenantPackageService) : BaseController() {
    /**
     * 查询租户套餐列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:list")
    @GetMapping("/list")
    fun list(bo: SysTenantPackageBo, pageQuery: PageQuery): TableDataInfo<SysTenantPackageVo> {
        return tenantPackageService.queryPageList(bo, pageQuery)
    }

    /**
     * 查询租户套餐下拉选列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:list")
    @GetMapping("/selectList")
    fun selectList(): R<List<SysTenantPackageVo>> {
        return R.ok(tenantPackageService.selectList())
    }

    /**
     * 导出租户套餐列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:export")
    @Log(title = "租户套餐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    fun export(bo: SysTenantPackageBo, response: HttpServletResponse) {
        val list = tenantPackageService.queryList(bo)
        exportExcel(list, "租户套餐", SysTenantPackageVo::class.java, response)
    }

    /**
     * 获取租户套餐详细信息
     *
     * @param packageId 主键
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:query")
    @GetMapping("/{packageId}")
    fun getInfo(@PathVariable packageId: @NotNull(message = "主键不能为空") Long?): R<SysTenantPackageVo?> {
        return R.ok(tenantPackageService.queryById(packageId))
    }

    /**
     * 新增租户套餐
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:add")
    @Log(title = "租户套餐", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(
        @Validated(
            AddGroup::class
        ) @RequestBody bo: SysTenantPackageBo
    ): R<Void> {
        return toAjax(tenantPackageService.insertByBo(bo))
    }

    /**
     * 修改租户套餐
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:edit")
    @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(
        @Validated(EditGroup::class) @RequestBody bo: SysTenantPackageBo
    ): R<Void> {
        return toAjax(tenantPackageService.updateByBo(bo))
    }

    /**
     * 状态修改
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:edit")
    @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody bo: SysTenantPackageBo): R<Void> {
        return toAjax(tenantPackageService.updatePackageStatus(bo))
    }

    /**
     * 删除租户套餐
     *
     * @param packageIds 主键串
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:remove")
    @Log(title = "租户套餐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{packageIds}")
    fun remove(@PathVariable packageIds: Array<Long>): R<Void> {
        return toAjax(tenantPackageService.deleteWithValidByIds(listOf(*packageIds), true))
    }
}
