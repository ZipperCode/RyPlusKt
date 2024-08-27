package com.zipper.server.web.service

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ObjectUtil
import com.zipper.framework.core.constant.Constants
import com.zipper.framework.core.constant.GlobalConstants
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.domain.dto.RoleDTO
import com.zipper.framework.core.domain.model.LoginUser
import com.zipper.framework.core.enums.LoginType
import com.zipper.framework.core.enums.TenantStatus
import com.zipper.framework.core.exception.user.UserException
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.DateUtilsExt.nowDate
import com.zipper.framework.core.utils.MessageUtils.message
import com.zipper.framework.core.utils.ServletUtils
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.framework.log.event.LoginLogEvent
import com.zipper.framework.mybatis.helper.DataPermissionHelper
import com.zipper.framework.redis.utils.RedisUtils
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.tanent.exception.TenantException
import com.zipper.framework.tanent.helper.TenantHelper
import com.zipper.modules.system.domain.bo.SysSocialBo
import com.zipper.modules.system.domain.entity.SysUserEntity
import com.zipper.modules.system.domain.vo.SysUserVo
import com.zipper.modules.system.mapper.SysUserMapper
import com.zipper.modules.system.service.permission.ISysPermissionService
import com.zipper.modules.system.service.social.ISysSocialService
import com.zipper.modules.tenant.service.ISysTenantService
import me.zhyd.oauth.model.AuthUser
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*
import java.util.function.Supplier

/**
 * 登录校验方法
 *
 * @author Lion Li
 */
@Service
class SysLoginService(
    private val tenantService: ISysTenantService,
    private val permissionService: ISysPermissionService,
    private val sysSocialService: ISysSocialService,
    private val userMapper: SysUserMapper
) {
    @Value("\${user.password.maxRetryCount}")
    private var maxRetryCount: Int = 5

    @Value("\${user.password.lockTime}")
    private var lockTime: Int = 10


    /**
     * 绑定第三方用户
     *
     * @param authUserData 授权响应实体
     * @return 统一响应实体
     */
    fun socialRegister(authUserData: AuthUser) {
        val authId = authUserData.source + authUserData.uuid
        // 第三方用户信息
        val bo = BeanUtil.toBean(authUserData, SysSocialBo::class.java)
        BeanUtil.copyProperties(authUserData.token, bo)
        bo.userId = LoginHelper.getUserId()
        bo.authId = authId
        bo.openId = authUserData.uuid
        bo.userName = authUserData.username
        bo.nickName = authUserData.nickname
        // 查询是否已经绑定用户
        val list = sysSocialService.selectByAuthId(authId)
        if (CollUtil.isEmpty(list)) {
            // 没有绑定用户, 新增用户信息
            sysSocialService.insertByBo(bo)
        } else {
            // 更新用户信息
            bo.id = list[0].id
            sysSocialService.updateByBo(bo)
        }
    }


    /**
     * 退出登录
     */
    fun logout() {
        try {
            val loginUser = LoginHelper.getLoginUser()
            if (ObjectUtil.isNull(loginUser)) {
                return
            }
            if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
                // 超级管理员 登出清除动态租户
                TenantHelper.clearDynamic()
            }
            recordLogininfor(loginUser!!.tenantId, loginUser.username, Constants.LOGOUT, message("user.logout.success"))
        } catch (ignored: NotLoginException) {
        } finally {
            try {
                StpUtil.logout()
            } catch (ignored: NotLoginException) {
            }
        }
    }

    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     */
    fun recordLogininfor(tenantId: String?, username: String?, status: String?, message: String?) {
        SpringUtilExt.context().publishEvent(
            LoginLogEvent(
                tenantId = tenantId,
                username = username,
                status = status,
                message = message,
                request = ServletUtils.getRequest()
            )
        )
    }


    /**
     * 构建登录用户
     */
    fun buildLoginUser(user: SysUserVo): LoginUser {
        val loginUser = LoginUser()
        loginUser.tenantId = user.tenantId
        loginUser.userId = user.userId
        loginUser.deptId = user.deptId
        loginUser.username = user.userName
        loginUser.nickname = user.nickName
        loginUser.userType = user.userType
        loginUser.menuPermission = permissionService.getMenuPermission(user.userId)
        loginUser.rolePermission = permissionService.getRolePermission(user.userId)
        loginUser.deptName = if (ObjectUtil.isNull(user.dept)) "" else user.dept!!.deptName
        val roles = BeanUtil.copyToList(user.roles, RoleDTO::class.java)
        loginUser.roles = roles
        return loginUser
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    fun recordLoginInfo(userId: Long?, ip: String?) {
        val sysUser = SysUserEntity()
        sysUser.userId = userId
        sysUser.loginIp = ip
        sysUser.loginDate = nowDate
        sysUser.updateBy = userId
        DataPermissionHelper.ignore<Int> { userMapper.updateById(sysUser) }
    }

    /**
     * 登录校验
     */
    fun checkLogin(loginType: LoginType, tenantId: String?, username: String?, supplier: Supplier<Boolean>) {
        val errorKey = GlobalConstants.PWD_ERR_CNT_KEY + username
        val loginFail = Constants.LOGIN_FAIL

        // 获取用户登录错误次数，默认为0 (可自定义限制策略 例如: key + username + ip)
        var errorNumber: Int = ObjectUtil.defaultIfNull(RedisUtils.getCacheObject(errorKey), 0)
        // 锁定时间内登录 则踢出
        if (errorNumber >= maxRetryCount) {
            recordLogininfor(tenantId, username, loginFail, message(loginType.retryLimitExceed, maxRetryCount, lockTime))
            throw UserException(loginType.retryLimitExceed, maxRetryCount, lockTime)
        }

        if (supplier.get()) {
            // 错误次数递增
            errorNumber++
            RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime!!.toLong()))
            // 达到规定错误次数 则锁定登录
            if (errorNumber >= maxRetryCount) {
                recordLogininfor(tenantId, username, loginFail, message(loginType.retryLimitExceed, maxRetryCount, lockTime))
                throw UserException(loginType.retryLimitExceed, maxRetryCount, lockTime)
            } else {
                // 未达到规定错误次数
                recordLogininfor(tenantId, username, loginFail, message(loginType.retryLimitCount, errorNumber))
                throw UserException(loginType.retryLimitCount, errorNumber)
            }
        }

        // 登录成功 清空错误次数
        RedisUtils.deleteObject(errorKey)
    }

    /**
     * 校验租户
     *
     * @param tenantId 租户ID
     */
    fun checkTenant(tenantId: String?) {
        if (!TenantHelper.isEnable()) {
            return
        }
        if (TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            return
        }
        if (StringUtils.isBlank(tenantId)) {
            throw TenantException("tenant.number.not.blank")
        }
        val tenant = tenantService.queryByTenantId(tenantId)
        if (ObjectUtil.isNull(tenant)) {
            log.info("登录租户：{} 不存在.", tenantId)
            throw TenantException("tenant.not.exists")
        } else if (TenantStatus.DISABLE.code == tenant!!.status) {
            log.info("登录租户：{} 已被停用.", tenantId)
            throw TenantException("tenant.blocked")
        } else if (ObjectUtil.isNotNull(tenant.expireTime)
            && Date().after(tenant.expireTime)
        ) {
            log.info("登录租户：{} 已超过有效期.", tenantId)
            throw TenantException("tenant.expired")
        }
    }
}
