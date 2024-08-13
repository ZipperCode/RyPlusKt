package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.hutool.core.convert.Convert
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysDeptBo
import com.zipper.modules.system.domain.vo.SysDeptVo
import com.zipper.modules.system.service.dept.ISysDeptService
import org.apache.commons.lang3.StringUtils
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.utils.StringUtilsExt
import java.util.function.Predicate

/**
 * 部门信息
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/system/dept")
class SysDeptController(private val deptService: ISysDeptService) : BaseController() {
    /**
     * 获取部门列表
     */
    @SaCheckPermission("system:dept:list")
    @GetMapping("/list")
    fun list(dept: SysDeptBo?): R<List<SysDeptVo>> {
        val depts = deptService.selectDeptList(dept!!)
        return R.ok(depts)
    }

    /**
     * 查询部门列表（排除节点）
     *
     * @param deptId 部门ID
     */
    @SaCheckPermission("system:dept:list")
    @GetMapping("/list/exclude/{deptId}")
    fun excludeChild(@PathVariable(value = "deptId", required = false) deptId: Long): R<List<SysDeptVo>> {
        val depts = deptService.selectDeptList(SysDeptBo())
        depts.toMutableSet().removeIf {
            deptId == it.deptId || StringUtils.contains(it.ancestors, Convert.toStr(deptId))
        }
        return R.ok(depts)
    }

    /**
     * 根据部门编号获取详细信息
     *
     * @param deptId 部门ID
     */
    @SaCheckPermission("system:dept:query")
    @GetMapping(value = ["/{deptId}"])
    fun getInfo(@PathVariable deptId: Long?): R<SysDeptVo?> {
        deptService.checkDeptDataScope(deptId)
        return R.ok(deptService.selectDeptById(deptId))
    }

    /**
     * 新增部门
     */
    @SaCheckPermission("system:dept:add")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    fun add(@Validated @RequestBody dept: SysDeptBo): R<Void> {
        if (!deptService.checkDeptNameUnique(dept)) {
            return R.fail("新增部门'" + dept.deptName + "'失败，部门名称已存在")
        }
        return toAjax(deptService.insertDept(dept))
    }

    /**
     * 修改部门
     */
    @SaCheckPermission("system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    fun edit(@Validated @RequestBody dept: SysDeptBo): R<Void> {
        val deptId = dept.deptId
        deptService.checkDeptDataScope(deptId)
        if (!deptService.checkDeptNameUnique(dept)) {
            return R.fail("修改部门'" + dept.deptName + "'失败，部门名称已存在")
        } else if (dept.parentId == deptId) {
            return R.fail("修改部门'" + dept.deptName + "'失败，上级部门不能是自己")
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.status)) {
            if (deptService.selectNormalChildrenDeptById(deptId) > 0) {
                return R.fail("该部门包含未停用的子部门!")
            } else if (deptService.checkDeptExistUser(deptId)) {
                return R.fail("该部门下存在已分配用户，不能禁用!")
            }
        }
        return toAjax(deptService.updateDept(dept))
    }

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     */
    @SaCheckPermission("system:dept:remove")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    fun remove(@PathVariable deptId: Long?): R<Void> {
        if (deptService.hasChildByDeptId(deptId)) {
            return R.warn("存在下级部门,不允许删除")
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return R.warn("部门存在用户,不允许删除")
        }
        deptService.checkDeptDataScope(deptId)
        return toAjax(deptService.deleteDeptById(deptId))
    }
}
