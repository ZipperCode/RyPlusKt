package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.tanent.core.TenantEntity
import lombok.Data
import lombok.EqualsAndHashCode
import com.zipper.framework.core.constant.UserConstants

/**
 * 字典数据表 sys_dict_data
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
class SysDictDataEntity : TenantEntity() {
    /**
     * 字典编码
     */
    @field:TableId(value = "dict_code")
    var dictCode: Long? = null

    /**
     * 字典排序
     */
    var dictSort: Int? = null

    /**
     * 字典标签
     */
    var dictLabel: String? = null

    /**
     * 字典键值
     */
    var dictValue: String? = null

    /**
     * 字典类型
     */
    var dictType: String? = null

    /**
     * 样式属性（其他样式扩展）
     */
    var cssClass: String? = null

    /**
     * 表格字典样式
     */
    var listClass: String? = null

    /**
     * 是否默认（Y是 N否）
     */

    var isDefault: String? = null

    fun setIsDefault(isDefault: String?) {
        this.isDefault = isDefault
    }

    fun getIsDefault(): String? = this.isDefault

    /**
     * 备注
     */
    var remark: String? = null
}
