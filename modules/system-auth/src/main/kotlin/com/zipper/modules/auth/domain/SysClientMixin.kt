package com.zipper.modules.auth.domain

import com.zipper.framework.mybatis.core.domain.CreatorMixin
import com.zipper.framework.mybatis.core.domain.UpdaterMixin
import java.io.Serializable

interface SysClientMixin : CreatorMixin, UpdaterMixin, Serializable {

    /**
     * id
     */
    var id: Long?

    /**
     * 客户端id
     */
    var clientId: String?

    /**
     * 客户端key
     */
    var clientKey: String?

    /**
     * 客户端秘钥
     */
    var clientSecret: String?

    /**
     * 授权类型
     */
    var grantType: String?

    /**
     * 设备类型
     */
    var deviceType: String?

    /**
     * token活跃超时时间
     */
    var activeTimeout: Long?

    /**
     * token固定超时时间
     */
    var timeout: Long?

    /**
     * 状态（0正常 1停用）
     */
    var status: String?
}