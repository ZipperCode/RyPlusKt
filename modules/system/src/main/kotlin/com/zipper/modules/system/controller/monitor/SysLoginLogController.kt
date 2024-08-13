package com.zipper.modules.system.controller.monitor

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.redis.utils.RedisUtils
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysLoginLogBo
import com.zipper.modules.system.domain.vo.SysLoginLogVo
import com.zipper.modules.system.service.log.ISysLoginLogService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import com.zipper.framework.core.constant.GlobalConstants
import org.zipper.framework.excel.utils.ExcelUtil.exportExcel

/**
 * 系统访问记录
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/monitor/logininfor")
class SysLoginLogController(
    private val loginLogService: ISysLoginLogService
) : BaseController() {
    /**
     * 获取系统访问记录列表
     */
    @SaCheckPermission("monitor:logininfor:list")
    @GetMapping("/list")
    fun list(loginLogBo: SysLoginLogBo, pageQuery: PageQuery): TableDataInfo<SysLoginLogVo> {
        return loginLogService.selectPageLoginLogList(loginLogBo, pageQuery)
    }

    /**
     * 导出系统访问记录列表
     */
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:logininfor:export")
    @PostMapping("/export")
    fun export(logininfor: SysLoginLogBo, response: HttpServletResponse) {
        val list = loginLogService.selectLoginLogList(logininfor)
        exportExcel(list, "登录日志", SysLoginLogVo::class.java, response)
    }

    /**
     * 批量删除登录日志
     * @param infoIds 日志ids
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    fun remove(@PathVariable infoIds: Array<Long>): R<Void> {
        return toAjax(loginLogService.deleteLoginLogByIds(infoIds))
    }

    /**
     * 清理系统访问记录
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    fun clean(): R<Void> {
        loginLogService.cleanLoginLog()
        return R.ok()
    }

    @SaCheckPermission("monitor:logininfor:unlock")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    fun unlock(@PathVariable("userName") userName: String): R<Void> {
        val loginName: String = GlobalConstants.PWD_ERR_CNT_KEY + userName
        if (RedisUtils.hasKey(loginName)) {
            RedisUtils.deleteObject(loginName)
        }
        return R.ok()
    }
}
