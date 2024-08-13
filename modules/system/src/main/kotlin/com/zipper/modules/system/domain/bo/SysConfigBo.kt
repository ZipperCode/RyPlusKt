package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysConfigEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 参数配置业务对象 sys_config
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysConfigEntity::class, reverseConvertGenerate = false)
class SysConfigBo : BaseEntity() {
    /**
     * 参数主键
     */
    var configId: Long? = null

    /**
     * 参数名称
     */
    @field:NotBlank(message = "参数名称不能为空")
    @field:Size(
        min = 0,
        max = 100,
        message = "参数名称不能超过{max}个字符"
    )
    var configName: String? = null

    /**
     * 参数键名
     */
    @field:NotBlank(message = "参数键名不能为空")
    @field:Size(
        min = 0,
        max = 100,
        message = "参数键名长度不能超过{max}个字符"
    )
    var configKey: String? = null

    /**
     * 参数键值
     */
    @field:NotBlank(message = "参数键值不能为空")
    @field:Size(
        min = 0,
        max = 500,
        message = "参数键值长度不能超过{max}个字符"
    )
    var configValue: String? = null

    /**
     * 系统内置（Y是 N否）
     */
    var configType: String? = null

    /**
     * 备注
     */
    var remark: String? = null
}
