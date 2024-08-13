package com.zipper.framework.satoken.utils

import cn.dev33.satoken.context.SaHolder
import cn.dev33.satoken.context.model.SaStorage
import cn.dev33.satoken.session.SaSession
import cn.dev33.satoken.stp.SaLoginModel
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import com.zipper.framework.core.domain.model.LoginUser
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.enums.UserType
import java.util.function.Supplier

/**
 * 登录鉴权助手
 *
 *
 * user_type 为 用户类型 同一个用户表 可以有多种用户类型 例如 pc,app
 * deivce 为 设备类型 同一个用户类型 可以有 多种设备类型 例如 web,ios
 * 可以组成 用户类型与设备类型多对多的 权限灵活控制
 *
 *
 * 多用户体系 针对 多种用户类型 但权限控制不一致
 * 可以组成 多用户类型表与多设备类型 分别控制权限
 *
 * @author Lion Li
 */
object LoginHelper {
    private const val LOGIN_USER_KEY: String = "loginUser"
    private const val TENANT_KEY: String = "tenantId"
    private const val USER_KEY: String = "userId"
    private const val DEPT_KEY: String = "deptId"
    const val CLIENT_KEY: String = "clientid"
    private const val TENANT_ADMIN_KEY: String = "isTenantAdmin"

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param loginUser 登录用户信息
     * @param model     配置参数
     */
    fun login(loginUser: LoginUser, model: SaLoginModel) {
        val storage: SaStorage = SaHolder.getStorage()
        storage.set(LOGIN_USER_KEY, loginUser)
        storage.set(TENANT_KEY, loginUser.tenantId)
        storage.set(USER_KEY, loginUser.userId)
        storage.set(DEPT_KEY, loginUser.deptId)
        StpUtil.login(
            loginUser.loginId,
            model.setExtra(TENANT_KEY, loginUser.tenantId)
                .setExtra(USER_KEY, loginUser.userId)
                .setExtra(DEPT_KEY, loginUser.deptId)
        )
        val tokenSession: SaSession = StpUtil.getTokenSession()
        tokenSession.updateTimeout(model.getTimeout())
        tokenSession.set(LOGIN_USER_KEY, loginUser)
    }

    fun getLoginUser(): LoginUser? {
        return getStorageIfAbsentSet(LOGIN_USER_KEY) {
            val session: SaSession = StpUtil.getTokenSession()
            if (ObjectUtil.isNull(session)) {
                return@getStorageIfAbsentSet null
            }
            session.get(LOGIN_USER_KEY)
        } as LoginUser?
    }

    /**
     * 获取用户基于token
     */
    fun getLoginUser(token: String?): LoginUser? {
        val session: SaSession = StpUtil.getTokenSessionByToken(token)
        if (ObjectUtil.isNull(session)) {
            return null
        }
        return session.get(LOGIN_USER_KEY) as LoginUser
    }

    /**
     * 获取用户id
     */
    fun getUserId(): Long = Convert.toLong(getExtra(USER_KEY))

    /**
     * 获取租户ID
     */
    fun getTenantId(): String? = Convert.toStr(getExtra(TENANT_KEY))

    /**
     * 获取部门ID
     */
    fun getDeptId(): Long = Convert.toLong(getExtra(DEPT_KEY))

    private fun getExtra(key: String?): Any? {
        return getStorageIfAbsentSet(key) { StpUtil.getExtra(key) }
    }

    /**
     * 获取用户账户
     */
    fun getUserName(): String? = getLoginUser()?.username

    /**
     * 获取用户类型
     */
    fun getUserType(): UserType {
        val loginType: String = StpUtil.getLoginIdAsString()
        return UserType.getUserType(loginType)
    }

    /**
     * 是否为超级管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    fun isSuperAdmin(userId: Long?): Boolean {
        return UserConstants.SUPER_ADMIN_ID == userId
    }

    fun isSuperAdmin(): Boolean = isSuperAdmin(getUserId())

    /**
     * 是否为超级管理员
     *
     * @param rolePermission 角色权限标识组
     * @return 结果
     */
    fun isTenantAdmin(rolePermission: Set<String?>?): Boolean {
        return rolePermission?.contains(TenantConstants.TENANT_ADMIN_ROLE_KEY) ?: false
    }

    fun isTenantAdmin(): Boolean {
        val value = getStorageIfAbsentSet(TENANT_ADMIN_KEY) {
            isTenantAdmin(getLoginUser()?.rolePermission)
        }
        return Convert.toBool(value)
    }

    fun isLogin(): Boolean = getLoginUser() != null

    fun getStorageIfAbsentSet(key: String?, handle: Supplier<Any?>): Any? {
        try {
            var obj = SaHolder.getStorage().get(key)
            if (ObjectUtil.isNull(obj)) {
                obj = handle.get()
                SaHolder.getStorage().set(key, obj)
            }
            return obj
        } catch (e: Exception) {
            return null
        }
    }
}
