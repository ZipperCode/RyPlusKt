package com.zipper.framework.oss.core

import com.zipper.framework.core.annotation.NoArgs


/**
 * OSS对象存储 配置属性
 *
 * @author Lion Li
 */
@NoArgs
data class OssConfig(
    /**
     * 访问站点
     */
    var endpoint: String,

    /**
     * 自定义域名
     */
    var domain: String,

    /**
     * ACCESS_KEY
     */
    var accessKey: String,

    /**
     * SECRET_KEY
     */
    var secretKey: String,

    /**
     * 存储空间名
     */
    var bucketName: String,

    /**
     * 存储区域
     */
    var region: String,

    /**
     * 前缀
     */
    var prefix: String = "",

    /**
     * 是否https（Y=是,N=否）
     */
    var isHttps: String = "N",

    /**
     * 桶权限类型(0private 1public 2custom)
     */
    var accessPolicy: String = "0"
) {

    var tenantId: String = ""
}
