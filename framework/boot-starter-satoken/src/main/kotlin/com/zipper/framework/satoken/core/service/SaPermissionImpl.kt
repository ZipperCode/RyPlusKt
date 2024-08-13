package com.zipper.framework.satoken.core.service

import cn.dev33.satoken.stp.StpInterface
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.satoken.utils.LoginHelper.getLoginUser
import com.zipper.framework.core.enums.UserType

/**
 * sa-token 权限管理实现类
 *
 * @author Lion Li
 */
class SaPermissionImpl : StpInterface {
    /**
     * 获取菜单权限列表
     */
    override fun getPermissionList(loginId: Any, loginType: String): List<String> {
        val loginUser = getLoginUser() ?: return emptyList()
        val userType = UserType.getUserType(loginUser.userType)
        if (userType == UserType.SYS_USER) {
            return ArrayList(loginUser.menuPermission)
        } else if (userType == UserType.APP_USER) {
            // 其他端 自行根据业务编写
        }
        return ArrayList()
    }

    /**
     * 获取角色权限列表
     */
    override fun getRoleList(loginId: Any, loginType: String): List<String> {
        val loginUser = getLoginUser() ?: return emptyList()
        val userType = UserType.getUserType(loginUser.userType)
        if (userType == UserType.SYS_USER) {
            return ArrayList(loginUser.rolePermission)
        } else if (userType == UserType.APP_USER) {
            // 其他端 自行根据业务编写
        }
        return ArrayList()
    }
}
