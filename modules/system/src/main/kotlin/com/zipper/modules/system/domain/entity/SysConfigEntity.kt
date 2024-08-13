package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.tanent.core.TenantEntity
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 参数配置表 sys_config
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
class SysConfigEntity : TenantEntity() {
    /**
     * 参数主键
     */
    @field:TableId(value = "config_id")
    var configId: Long? = null

    /**
     * 参数名称
     */
    var configName: String? = null

    /**
     * 参数键名
     */
    var configKey: String? = null

    /**
     * 参数键值
     */
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
