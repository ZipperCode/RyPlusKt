package com.zipper.modules.system.controller.monitor

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysOperLogBo
import com.zipper.modules.system.domain.vo.SysOperLogVo
import com.zipper.modules.system.service.log.ISysOperLogService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.zipper.framework.excel.utils.ExcelUtil.exportExcel

/**
 * 操作日志记录
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/monitor/operlog")
class SysOperlogController(private val operLogService: ISysOperLogService) : BaseController() {
    /**
     * 获取操作日志记录列表
     */
    @SaCheckPermission("monitor:operlog:list")
    @GetMapping("/list")
    fun list(operLog: SysOperLogBo, pageQuery: PageQuery): TableDataInfo<SysOperLogVo> {
        return operLogService.selectPageOperLogList(operLog, pageQuery)
    }

    /**
     * 导出操作日志记录列表
     */
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:operlog:export")
    @PostMapping("/export")
    fun export(operLog: SysOperLogBo, response: HttpServletResponse) {
        val list = operLogService.selectOperLogList(operLog)
        exportExcel(list, "操作日志", SysOperLogVo::class.java, response)
    }

    /**
     * 批量删除操作日志记录
     * @param operIds 日志ids
     */
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @SaCheckPermission("monitor:operlog:remove")
    @DeleteMapping("/{operIds}")
    fun remove(@PathVariable operIds: Array<Long>): R<Void> {
        return toAjax(operLogService.deleteOperLogByIds(operIds))
    }

    /**
     * 清理操作日志记录
     */
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @SaCheckPermission("monitor:operlog:remove")
    @DeleteMapping("/clean")
    fun clean(): R<Void> {
        operLogService.cleanOperLog()
        return R.ok()
    }
}
