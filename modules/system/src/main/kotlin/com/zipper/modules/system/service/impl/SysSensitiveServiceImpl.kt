package com.zipper.modules.system.service.impl

import cn.dev33.satoken.stp.StpUtil
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.sensitive.core.SensitiveService
import com.zipper.framework.tanent.helper.TenantHelper
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service

/**
 * 脱敏服务
 * 默认管理员不过滤
 * 需自行根据业务重写实现
 *
 * @author Lion Li
 * @version 3.6.0
 */
@Service
class SysSensitiveServiceImpl : SensitiveService {
    /**
     * 是否脱敏
     */
    override fun isSensitive(roleKey: String?, perms: String?): Boolean {
        if (!LoginHelper.isLogin()) {
            return true
        }
        val roleExist = StringUtils.isNotBlank(roleKey)
        val permsExist = StringUtils.isNotBlank(perms)
        if (roleExist && permsExist) {
            if (StpUtil.hasRole(roleKey) && StpUtil.hasPermission(perms)) {
                return false
            }
        } else if (roleExist && StpUtil.hasRole(roleKey)) {
            return false
        } else if (permsExist && StpUtil.hasPermission(perms)) {
            return false
        }

        if (TenantHelper.isEnable()) {
            return !LoginHelper.isSuperAdmin() && !LoginHelper.isTenantAdmin()
        }
        return !LoginHelper.isSuperAdmin()
    }
}
