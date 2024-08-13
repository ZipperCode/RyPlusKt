package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.zipper.modules.system.domain.entity.SysOssConfigEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import java.io.Serial
import java.io.Serializable

/**
 * 对象存储配置视图对象 sys_oss_config
 *
 * @author Lion Li
 * @author 孤舟烟雨
 * @date 2021-08-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysOssConfigEntity::class)
class SysOssConfigVo : Serializable {
    /**
     * 主建
     */
    var ossConfigId: Long? = null

    /**
     * 配置key
     */
    var configKey: String? = null

    /**
     * accessKey
     */
    var accessKey: String? = null

    /**
     * 秘钥
     */
    var secretKey: String? = null

    /**
     * 桶名称
     */
    var bucketName: String? = null

    /**
     * 前缀
     */
    var prefix: String? = null

    /**
     * 访问站点
     */
    var endpoint: String? = null

    /**
     * 自定义域名
     */
    var domain: String? = null

    /**
     * 是否https（Y=是,N=否）
     */
    var isHttps: String? = null

    fun setIsHttps(isHttps: String?) {
        this.isHttps = isHttps
    }
    fun getIsHttps(): String? = this.isHttps
    /**
     * 域
     */
    var region: String? = null

    /**
     * 是否默认（0=是,1=否）
     */
    var status: String? = null

    /**
     * 扩展字段
     */
    var ext1: String? = null

    /**
     * 备注
     */
    var remark: String? = null

    /**
     * 桶权限类型(0private 1public 2custom)
     */
    var accessPolicy: String? = null
}
