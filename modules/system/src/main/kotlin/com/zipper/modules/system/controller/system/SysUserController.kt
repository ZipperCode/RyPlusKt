package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.secure.BCrypt
import cn.hutool.core.lang.tree.Tree
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.ObjectUtil
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.tanent.helper.TenantHelper
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysDeptBo
import com.zipper.modules.system.domain.bo.SysPostBo
import com.zipper.modules.system.domain.bo.SysRoleBo
import com.zipper.modules.system.domain.bo.SysUserBo
import com.zipper.modules.system.domain.vo.*
import com.zipper.modules.system.listener.SysUserImportListener
import com.zipper.modules.system.service.dept.ISysDeptService
import com.zipper.modules.system.service.post.ISysPostService
import com.zipper.modules.system.service.role.ISysRoleService
import com.zipper.modules.system.service.tenant.ISysTenantService
import com.zipper.modules.system.service.user.ISysUserService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotNull
import org.apache.commons.lang3.StringUtils
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.utils.MapstructUtils.convertWithClass
import org.zipper.framework.excel.utils.ExcelUtil.exportExcel
import org.zipper.framework.excel.utils.ExcelUtil.importExcel

/**
 * 用户信息
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/system/user")
class SysUserController(
    private val userService: ISysUserService,
    private val roleService: ISysRoleService,
    private val postService: ISysPostService,
    private val deptService: ISysDeptService,
    private val tenantService: ISysTenantService
) : BaseController() {
    /**
     * 获取用户列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/list")
    fun list(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        return userService.selectPageUserList(user, pageQuery)
    }

    /**
     * 导出用户列表
     */
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:user:export")
    @PostMapping("/export")
    fun export(user: SysUserBo, response: HttpServletResponse) {
        val list = userService.selectUserList(user)
        val listVo = convertWithClass<SysUserVo, SysUserExportVo>(list, SysUserExportVo::class.java)
        exportExcel(listVo, "用户数据", SysUserExportVo::class.java, response)
    }

    /**
     * 导入数据
     *
     * @param file          导入文件
     * @param updateSupport 是否更新已存在数据
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @SaCheckPermission("system:user:import")
    @PostMapping(value = ["/importData"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun importData(@RequestPart("file") file: MultipartFile, updateSupport: Boolean): R<Void> {
        val result = importExcel(file.inputStream, SysUserImportVo::class.java, SysUserImportListener(updateSupport))
        return R.ok(result.getAnalysis())
    }

    /**
     * 获取导入模板
     */
    @PostMapping("/importTemplate")
    fun importTemplate(response: HttpServletResponse) {
        exportExcel(ArrayList(), "用户数据", SysUserImportVo::class.java, response)
    }

    @get:GetMapping("/getInfo")
    val info: R<UserInfoVo>
        /**
         * 获取用户信息
         *
         * @return 用户信息
         */
        get() {
            val userInfoVo = UserInfoVo()
            val loginUser = LoginHelper.getLoginUser()
            if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
                // 超级管理员 如果重新加载用户信息需清除动态租户
                TenantHelper.clearDynamic()
            }
            val user = userService.selectUserById(loginUser?.userId)
            if (ObjectUtil.isNull(user)) {
                return R.fail("没有权限访问用户数据!")
            }
            userInfoVo.user = user
            userInfoVo.permissions = loginUser?.menuPermission
            userInfoVo.roles = loginUser?.rolePermission
            return R.ok(userInfoVo)
        }

    /**
     * 根据用户编号获取详细信息
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping(value = ["/", "/{userId}"])
    fun getInfo(@PathVariable(value = "userId", required = false) userId: Long?): R<SysUserInfoVo> {
        userService.checkUserDataScope(userId)
        val userInfoVo = SysUserInfoVo()
        val roleBo = SysRoleBo()
        roleBo.status = UserConstants.ROLE_NORMAL
        val postBo = SysPostBo()
        postBo.status = UserConstants.POST_NORMAL
        val roles = roleService.selectRoleList(roleBo)
        userInfoVo.roles = if (LoginHelper.isSuperAdmin(userId)) roles else roles.filter { !it.isSuperAdmin }
        userInfoVo.posts = postService.selectPostList(postBo)
        if (ObjectUtil.isNotNull(userId)) {
            val sysUser = userService.selectUserById(userId)
            userInfoVo.user = sysUser
            userInfoVo.roleIds = sysUser?.roles?.mapNotNull { it.roleId }
            userInfoVo.postIds = postService.selectPostListByUserId(userId)
        }
        return R.ok(userInfoVo)
    }

    /**
     * 新增用户
     */
    @SaCheckPermission("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    fun add(@Validated @RequestBody user: SysUserBo): R<Void> {
        deptService.checkDeptDataScope(user.deptId)
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("新增用户'" + user.userName + "'失败，登录账号已存在")
        } else if (StringUtils.isNotEmpty(user.phonenumber) && !userService.checkPhoneUnique(user)) {
            return R.fail("新增用户'" + user.userName + "'失败，手机号码已存在")
        } else if (StringUtils.isNotEmpty(user.email) && !userService.checkEmailUnique(user)) {
            return R.fail("新增用户'" + user.userName + "'失败，邮箱账号已存在")
        }
        if (TenantHelper.isEnable()) {
            if (!tenantService.checkAccountBalance(TenantHelper.tenantId)) {
                return R.fail("当前租户下用户名额不足，请联系管理员")
            }
        }
        user.password = BCrypt.hashpw(user.password)
        return toAjax(userService.insertUser(user))
    }

    /**
     * 修改用户
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    fun edit(@Validated @RequestBody user: SysUserBo): R<Void> {
        userService.checkUserAllowed(user.userId)
        userService.checkUserDataScope(user.userId)
        deptService.checkDeptDataScope(user.deptId)
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("修改用户'" + user.userName + "'失败，登录账号已存在")
        } else if (StringUtils.isNotEmpty(user.phonenumber) && !userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'" + user.userName + "'失败，手机号码已存在")
        } else if (StringUtils.isNotEmpty(user.email) && !userService.checkEmailUnique(user)) {
            return R.fail("修改用户'" + user.userName + "'失败，邮箱账号已存在")
        }
        return toAjax(userService.updateUser(user))
    }

    /**
     * 删除用户
     *
     * @param userIds 角色ID串
     */
    @SaCheckPermission("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    fun remove(@PathVariable userIds: Array<Long>): R<Void> {
        if (ArrayUtil.contains(userIds, LoginHelper.getUserId())) {
            return R.fail("当前用户不能删除")
        }
        return toAjax(userService.deleteUserByIds(userIds))
    }

    /**
     * 重置密码
     */
    @com.zipper.framework.encrypt.annotation.ApiEncrypt
    @SaCheckPermission("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    fun resetPwd(@RequestBody user: SysUserBo): R<Void> {
        userService.checkUserAllowed(user.userId)
        userService.checkUserDataScope(user.userId)
        user.password = BCrypt.hashpw(user.password)
        return toAjax(userService.resetUserPwd(user.userId, user.password))
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    fun changeStatus(@RequestBody user: SysUserBo): R<Void> {
        userService.checkUserAllowed(user.userId)
        userService.checkUserDataScope(user.userId)
        return toAjax(userService.updateUserStatus(user.userId, user.status))
    }

    /**
     * 根据用户编号获取授权角色
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/authRole/{userId}")
    fun authRole(@PathVariable userId: Long?): R<SysUserInfoVo> {
        val user = userService.selectUserById(userId)
        val roles = roleService.selectRolesByUserId(userId)
        val userInfoVo = SysUserInfoVo()
        userInfoVo.user = user
        userInfoVo.roles = if (LoginHelper.isSuperAdmin(userId)) roles else roles.filter { !it.isSuperAdmin }
        return R.ok(userInfoVo)
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户Id
     * @param roleIds 角色ID串
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    fun insertAuthRole(userId: Long?, roleIds: Array<Long>): R<Void> {
        userService.checkUserDataScope(userId)
        userService.insertUserAuth(userId, roleIds)
        return R.ok()
    }

    /**
     * 获取部门树列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/deptTree")
    fun deptTree(dept: SysDeptBo): R<List<Tree<Long>>> {
        return R.ok(deptService.selectDeptTreeList(dept))
    }

    /**
     * 获取部门下的所有用户信息
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/list/dept/{deptId}")
    fun listByDept(@PathVariable deptId: @NotNull Long?): R<List<SysUserVo>> {
        return R.ok(userService.selectUserListByDept(deptId))
    }
}
