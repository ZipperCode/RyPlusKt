package com.zipper.framework.oss.properties


/**
 * OSS对象存储 配置属性
 *
 * @author Lion Li
 */
class OssProperties {
    /**
     * 租户id
     */
    var tenantId: String? = null

    /**
     * 访问站点
     */
    var endpoint: String? = null

    /**
     * 自定义域名
     */
    var domain: String? = null

    /**
     * 前缀
     */
    var prefix: String? = null

    /**
     * ACCESS_KEY
     */
    var accessKey: String? = null

    /**
     * SECRET_KEY
     */
    var secretKey: String? = null

    /**
     * 存储空间名
     */
    var bucketName: String = ""

    /**
     * 存储区域
     */
    var region: String? = null

    /**
     * 是否https（Y=是,N=否）
     */
    var isHttps: String = "N"

    /**
     * 桶权限类型(0private 1public 2custom)
     */
    var accessPolicy: String = "0"
}
