package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.tanent.core.TenantEntity
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 字典类型表 sys_dict_type
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
class SysDictTypeEntity : TenantEntity() {
    /**
     * 字典主键
     */
    @TableId(value = "dict_id", type = IdType.AUTO)
    var dictId: Long? = null

    /**
     * 字典名称
     */
    var dictName: String = ""

    /**
     * 字典类型
     */
    var dictType: String = "'"

    /**
     * 备注
     */
    var remark: String? = null
}
