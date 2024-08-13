package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysOssConfigEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import lombok.Data
import lombok.EqualsAndHashCode
import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup

/**
 * 对象存储配置业务对象 sys_oss_config
 *
 * @author Lion Li
 * @author 孤舟烟雨
 * @date 2021-08-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysOssConfigEntity::class, reverseConvertGenerate = false)
class SysOssConfigBo : BaseEntity() {
    /**
     * 主建
     */
    @field:NotNull(message = "主建不能为空", groups = [EditGroup::class])
    var ossConfigId: Long? = null

    /**
     * 配置key
     */
    @field:NotBlank(message = "配置key不能为空", groups = [AddGroup::class, EditGroup::class])
    @field:Size(
        min = 2,
        max = 100,
        message = "configKey长度必须介于{min}和{max} 之间"
    )
    var configKey: String? = null

    /**
     * accessKey
     */
    @field:NotBlank(message = "accessKey不能为空", groups = [AddGroup::class, EditGroup::class])
    @field:Size(
        min = 2,
        max = 100,
        message = "accessKey长度必须介于{min}和{max} 之间"
    )
    var accessKey: String? = null

    /**
     * 秘钥
     */
    @field:NotBlank(message = "secretKey不能为空", groups = [AddGroup::class, EditGroup::class])
    @field:Size(
        min = 2,
        max = 100,
        message = "secretKey长度必须介于{min}和{max} 之间"
    )
    var secretKey: String? = null

    /**
     * 桶名称
     */
    @field:NotBlank(message = "桶名称不能为空", groups = [AddGroup::class, EditGroup::class])
    @field:Size(
        min = 2,
        max = 100,
        message = "bucketName长度必须介于{min}和{max}之间"
    )
    var bucketName: String? = null

    /**
     * 前缀
     */
    var prefix: String? = null

    /**
     * 访问站点
     */
    @field:NotBlank(message = "访问站点不能为空", groups = [AddGroup::class, EditGroup::class])
    @field:Size(
        min = 2,
        max = 100,
        message = "endpoint长度必须介于{min}和{max}之间"
    )
    var endpoint: String? = null

    /**
     * 自定义域名
     */
    var domain: String? = null

    /**
     * 是否https（Y=是,N=否）
     */
    var isHttps: String? = null

    fun setIsHttps(isHttps: String) {
        this.isHttps = isHttps
    }

    fun getIsHttps():String? = this.isHttps

    /**
     * 是否默认（0=是,1=否）
     */
    var status: String? = null

    /**
     * 域
     */
    var region: String? = null

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
    @field:NotBlank(message = "桶权限类型不能为空", groups = [AddGroup::class, EditGroup::class])
    var accessPolicy: String? = null
}
