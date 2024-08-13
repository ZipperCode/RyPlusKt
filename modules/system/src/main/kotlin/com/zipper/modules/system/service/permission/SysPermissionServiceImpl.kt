package com.zipper.modules.system.service.permission

import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.modules.system.service.menu.ISysMenuService
import com.zipper.modules.system.service.role.ISysRoleService
import org.springframework.stereotype.Service
import com.zipper.framework.core.constant.TenantConstants

/**
 * 用户权限处理
 *
 * @author ruoyi
 */
@Service
class SysPermissionServiceImpl(
    private val roleService: ISysRoleService,
    private val menuService: ISysMenuService
) : ISysPermissionService {
    /**
     * 获取角色数据权限
     *
     * @param userId  用户id
     * @return 角色权限信息
     */
    override fun getRolePermission(userId: Long?): Set<String> {
        val roles: MutableSet<String> = HashSet()
        // 管理员拥有所有权限
        if (LoginHelper.isSuperAdmin(userId)) {
            roles.add(TenantConstants.SUPER_ADMIN_ROLE_KEY)
        } else {
            roles.addAll(roleService.selectRolePermissionByUserId(userId!!))
        }
        return roles
    }

    /**
     * 获取菜单数据权限
     *
     * @param userId  用户id
     * @return 菜单权限信息
     */
    override fun getMenuPermission(userId: Long?): Set<String> {
        val perms: MutableSet<String> = HashSet()
        // 管理员拥有所有权限
        if (LoginHelper.isSuperAdmin(userId)) {
            perms.add("*:*:*")
        } else {
            perms.addAll(menuService.selectMenuPermsByUserId(userId))
        }
        return perms
    }
}
