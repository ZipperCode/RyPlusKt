package com.zipper.framework.social.config.properties


/**
 * 社交登录配置
 *
 * @author thiszhc
 */
class SocialLoginConfigProperties {
    /**
     * 应用 ID
     */
    var clientId: String = ""

    /**
     * 应用密钥
     */
    var clientSecret:  String = ""

    /**
     * 回调地址
     */
    var redirectUri:  String = ""

    /**
     * 是否获取unionId
     */
    var unionId = false

    /**
     * Coding 企业名称
     */
    var codingGroupName:  String = ""

    /**
     * 支付宝公钥
     */
    var alipayPublicKey:  String = ""

    /**
     * 企业微信应用ID
     */
    var agentId:  String = ""

    /**
     * stackoverflow api key
     */
    var stackOverflowKey:  String = ""

    /**
     * 设备ID
     */
    var deviceId:  String = ""

    /**
     * 客户端系统类型
     */
    var clientOsType:  String = ""

    /**
     * maxkey 服务器地址
     */
    var serverUrl:  String = ""
}
