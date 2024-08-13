package com.zipper.modules.system.service.permission

/**
 * 用户权限处理
 *
 * @author Lion Li
 */
interface ISysPermissionService {
    /**
     * 获取角色数据权限
     *
     * @param userId  用户id
     * @return 角色权限信息
     */
    fun getRolePermission(userId: Long?): Set<String>

    /**
     * 获取菜单数据权限
     *
     * @param userId  用户id
     * @return 菜单权限信息
     */
    fun getMenuPermission(userId: Long?): Set<String>
}
