package com.zipper.modules.tenant.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaCheckRole
import com.zipper.framework.core.domain.R
import com.zipper.framework.idempotent.annotation.RepeatSubmit
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.tanent.helper.TenantHelper
import com.zipper.framework.web.core.BaseController
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import com.zipper.modules.tenant.domain.param.SysTenantQueryParam
import com.zipper.modules.tenant.domain.param.SysTenantSaveParam
import com.zipper.modules.tenant.domain.vo.SysTenantVo
import com.zipper.modules.tenant.service.ISysTenantService
import org.zipper.framework.excel.utils.ExcelUtil.exportExcel

/**
 * 租户管理
 *
 * @author Michelle.Chung
 */
@Validated
@RestController
@RequestMapping("/system/tenant")
class SysTenantController(
    private val tenantService: ISysTenantService
) : BaseController() {
    /**
     * 查询租户列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:list")
    @GetMapping("/list")
    fun list(param: SysTenantQueryParam, pageQuery: PageQuery): TableDataInfo<SysTenantVo> {
        return tenantService.queryPageList(param, pageQuery)
    }

    /**
     * 导出租户列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:export")
    @Log(title = "租户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    fun export(response: HttpServletResponse) {
        val list = tenantService.queryList()
        exportExcel(list, "租户", SysTenantVo::class.java, response)
    }

    /**
     * 获取租户详细信息
     *
     * @param id 主键
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:query")
    @GetMapping("/{id}")
    fun getInfo(@PathVariable id: @NotNull(message = "主键不能为空") Long?): R<SysTenantVo?> {
        return R.ok(tenantService.queryById(id))
    }

    /**
     * 新增租户
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:add")
    @Log(title = "租户", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated(AddGroup::class) @RequestBody param: SysTenantSaveParam): R<Void> {
        if (!tenantService.checkCompanyNameUnique(param)) {
            return R.fail("新增租户'" + param.companyName + "'失败，企业名称已存在")
        }
        return toAjax(TenantHelper.ignore { tenantService.insertByBo(param) })
    }

    /**
     * 修改租户
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(
        @Validated(EditGroup::class) @RequestBody param: SysTenantSaveParam
    ): R<Void> {
        tenantService.checkTenantAllowed(param.tenantId)
        if (!tenantService.checkCompanyNameUnique(param)) {
            return R.fail("修改租户'" + param.companyName + "'失败，公司名称已存在")
        }
        return toAjax(tenantService.updateByBo(param))
    }

    /**
     * 状态修改
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody param: SysTenantSaveParam): R<Void> {
        tenantService.checkTenantAllowed(param.tenantId)
        return toAjax(tenantService.updateTenantStatus(param))
    }

    /**
     * 删除租户
     *
     * @param ids 主键串
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:remove")
    @Log(title = "租户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    fun remove(@PathVariable ids: Array<Long>): R<Void> {
        return toAjax(tenantService.deleteWithValidByIds(listOf(*ids), true))
    }

    /**
     * 动态切换租户
     *
     * @param tenantId 租户ID
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @GetMapping("/dynamic/{tenantId}")
    fun dynamicTenant(@PathVariable tenantId: @NotBlank(message = "租户ID不能为空") String?): R<Void> {
        TenantHelper.dynamic = tenantId
        return R.ok()
    }

    /**
     * 清除动态租户
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @GetMapping("/dynamic/clear")
    fun dynamicClear(): R<Void> {
        TenantHelper.clearDynamic()
        return R.ok()
    }


    /**
     * 同步租户套餐
     *
     * @param tenantId  租户id
     * @param packageId 套餐id
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @GetMapping("/syncTenantPackage")
    fun syncTenantPackage(
        tenantId: @NotBlank(message = "租户ID不能为空") String,
        packageId: @NotNull(message = "套餐ID不能为空") Long?
    ): R<Void> {
        return toAjax(TenantHelper.ignore { tenantService.syncTenantPackage(tenantId, packageId) })
    }
}
