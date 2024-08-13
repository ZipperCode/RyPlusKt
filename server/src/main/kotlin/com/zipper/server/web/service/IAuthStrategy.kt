package com.zipper.server.web.service

import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.modules.system.domain.entity.SysClientEntity
import com.zipper.server.web.domain.vo.LoginVo


/**
 * 授权策略
 *
 * @author Michelle.Chung
 */
interface IAuthStrategy {
    /**
     * 登录
     */
    fun login(body: String, client: SysClientEntity): LoginVo

    companion object {
        /**
         * 登录
         */
        fun login(body: String, client: SysClientEntity, grantType: String): LoginVo {
            // 授权类型和客户端id
            val beanName = grantType + BASE_NAME
            if (!SpringUtilExt.containsBean(beanName)) {
                throw ServiceException("授权类型不正确!")
            }
            val instance: IAuthStrategy = SpringUtilExt.getBeanByName(beanName)
            return instance.login(body, client)
        }

        const val BASE_NAME: String = "AuthStrategy"
    }
}
