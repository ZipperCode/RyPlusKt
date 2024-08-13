package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysDeptBo
import com.zipper.modules.system.domain.bo.SysRoleBo
import com.zipper.modules.system.domain.bo.SysUserBo
import com.zipper.modules.system.domain.entity.SysUserRoleEntity
import com.zipper.modules.system.domain.vo.DeptTreeSelectVo
import com.zipper.modules.system.domain.vo.SysRoleVo
import com.zipper.modules.system.domain.vo.SysUserVo
import com.zipper.modules.system.service.dept.ISysDeptService
import com.zipper.modules.system.service.role.ISysRoleService
import com.zipper.modules.system.service.user.ISysUserService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.zipper.framework.excel.utils.ExcelUtil.exportExcel

/**
 * 角色信息
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/system/role")
class SysRoleController(
    private val roleService: ISysRoleService,
    private val userService: ISysUserService,
    private val deptService: ISysDeptService
) : BaseController() {
    /**
     * 获取角色信息列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/list")
    fun list(role: SysRoleBo, pageQuery: PageQuery): TableDataInfo<SysRoleVo> {
        return roleService.selectPageRoleList(role, pageQuery)
    }

    /**
     * 导出角色信息列表
     */
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:role:export")
    @PostMapping("/export")
    fun export(role: SysRoleBo, response: HttpServletResponse) {
        val list = roleService.selectRoleList(role)
        exportExcel(list, "角色数据", SysRoleVo::class.java, response)
    }

    /**
     * 根据角色编号获取详细信息
     *
     * @param roleId 角色ID
     */
    @SaCheckPermission("system:role:query")
    @GetMapping(value = ["/{roleId}"])
    fun getInfo(@PathVariable roleId: Long?): R<SysRoleVo?> {
        roleService.checkRoleDataScope(roleId ?: 0)
        return R.ok(roleService.selectRoleById(roleId))
    }

    /**
     * 新增角色
     */
    @SaCheckPermission("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    fun add(@Validated @RequestBody role: SysRoleBo): R<Void> {
        roleService.checkRoleAllowed(role)
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("新增角色'" + role.roleName + "'失败，角色名称已存在")
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("新增角色'" + role.roleName + "'失败，角色权限已存在")
        }
        return toAjax(roleService.insertRole(role))
    }

    /**
     * 修改保存角色
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    fun edit(@Validated @RequestBody role: SysRoleBo): R<Void> {
        roleService.checkRoleAllowed(role)
        roleService.checkRoleDataScope(role.roleId ?: 0)
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("修改角色'" + role.roleName + "'失败，角色名称已存在")
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("修改角色'" + role.roleName + "'失败，角色权限已存在")
        }

        if (roleService.updateRole(role) > 0) {
            roleService.cleanOnlineUserByRole(role.roleId)
            return R.ok()
        }
        return R.fail("修改角色'" + role.roleName + "'失败，请联系管理员")
    }

    /**
     * 修改保存数据权限
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    fun dataScope(@RequestBody role: SysRoleBo): R<Void> {
        roleService.checkRoleAllowed(role)
        roleService.checkRoleDataScope(role.roleId ?: 0)
        return toAjax(roleService.authDataScope(role))
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody role: SysRoleBo): R<Void> {
        roleService.checkRoleAllowed(role)
        roleService.checkRoleDataScope(role.roleId ?: 0)
        return toAjax(roleService.updateRoleStatus(role.roleId, role.status))
    }

    /**
     * 删除角色
     *
     * @param roleIds 角色ID串
     */
    @SaCheckPermission("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    fun remove(@PathVariable roleIds: Array<Long>): R<Void> {
        return toAjax(roleService.deleteRoleByIds(roleIds))
    }

    /**
     * 获取角色选择框列表
     */
    @SaCheckPermission("system:role:query")
    @GetMapping("/optionselect")
    fun optionselect(): R<List<SysRoleVo>> {
        return R.ok(roleService.selectRoleAll())
    }

    /**
     * 查询已分配用户角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/allocatedList")
    fun allocatedList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        return userService.selectAllocatedList(user, pageQuery)
    }

    /**
     * 查询未分配用户角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/unallocatedList")
    fun unallocatedList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        return userService.selectUnallocatedList(user, pageQuery)
    }

    /**
     * 取消授权用户
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    fun cancelAuthUser(@RequestBody userRole: SysUserRoleEntity): R<Void> {
        return toAjax(roleService.deleteAuthUser(userRole))
    }

    /**
     * 批量取消授权用户
     *
     * @param roleId  角色ID
     * @param userIds 用户ID串
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    fun cancelAuthUserAll(roleId: Long?, userIds: Array<Long>): R<Void> {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds))
    }

    /**
     * 批量选择用户授权
     *
     * @param roleId  角色ID
     * @param userIds 用户ID串
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    fun selectAuthUserAll(roleId: Long?, userIds: Array<Long>): R<Void> {
        roleService.checkRoleDataScope(roleId)
        return toAjax(roleService.insertAuthUsers(roleId, userIds))
    }

    /**
     * 获取对应角色部门树列表
     *
     * @param roleId 角色ID
     */
    @SaCheckPermission("system:role:list")
    @GetMapping(value = ["/deptTree/{roleId}"])
    fun roleDeptTreeselect(@PathVariable("roleId") roleId: Long?): R<DeptTreeSelectVo> {
        val selectVo = DeptTreeSelectVo()
        selectVo.checkedKeys = deptService.selectDeptListByRoleId(roleId)
        selectVo.depts = deptService.selectDeptTreeList(SysDeptBo())
        return R.ok(selectVo)
    }
}
