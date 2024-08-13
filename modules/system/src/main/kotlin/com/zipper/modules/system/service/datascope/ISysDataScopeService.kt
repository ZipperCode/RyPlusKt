package com.zipper.modules.system.service.datascope

/**
 * 通用 数据权限 服务
 *
 * @author Lion Li
 */
interface ISysDataScopeService {
    /**
     * 获取角色自定义权限
     *
     * @param roleId 角色id
     * @return 部门id组
     */
    fun getRoleCustom(roleId: Long?): String?

    /**
     * 获取部门及以下权限
     *
     * @param deptId 部门id
     * @return 部门id组
     */
    fun getDeptAndChild(deptId: Long?): String?
}
