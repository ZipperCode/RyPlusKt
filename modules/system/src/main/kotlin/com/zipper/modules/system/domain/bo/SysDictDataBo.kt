package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysDictDataEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 字典数据业务对象 sys_dict_data
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysDictDataEntity::class, reverseConvertGenerate = false)
class SysDictDataBo : BaseEntity() {
    /**
     * 字典编码
     */
    var dictCode: Long? = null

    /**
     * 字典排序
     */
    var dictSort: Int? = null

    /**
     * 字典标签
     */
    @field:NotBlank(message = "字典标签不能为空")
    @field:Size(
        min = 0,
        max = 100,
        message = "字典标签长度不能超过{max}个字符"
    )
    var dictLabel: String? = null

    /**
     * 字典键值
     */
    @field:NotBlank(message = "字典键值不能为空")
    @field:Size(
        min = 0,
        max = 100,
        message = "字典键值长度不能超过{max}个字符"
    )
    var dictValue: String? = null

    /**
     * 字典类型
     */
    @field:NotBlank(message = "字典类型不能为空")
    @field:Size(
        min = 0,
        max = 100,
        message = "字典类型长度不能超过{max}个字符"
    )
    var dictType: String? = null

    /**
     * 样式属性（其他样式扩展）
     */
    @field:Size(min = 0, max = 100, message = "样式属性长度不能超过{max}个字符")
    var cssClass: String? = null

    /**
     * 表格回显样式
     */
    var listClass: String? = null

    /**
     * 是否默认（Y是 N否）
     */
    var isDefault: String? = null

    fun setIsDefault(isDefault: String?) {
        this.isDefault = isDefault
    }
    fun getIsDefault(): String? {
        return isDefault
    }

    /**
     * 备注
     */
    var remark: String? = null
}
