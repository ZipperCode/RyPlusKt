package com.zipper.modules.auth.domain.param

class SysClientQueryParam {
    /**
     * 客户端id
     */
    var clientId: String? = null
    /**
     * 客户端key
     */
    var clientKey: String? = ""
    /**
     * 客户端秘钥
     */
    var clientSecret: String? = ""

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null
}