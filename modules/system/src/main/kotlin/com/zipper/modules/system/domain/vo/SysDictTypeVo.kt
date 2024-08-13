package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.modules.system.domain.entity.SysDictTypeEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 字典类型视图对象 sys_dict_type
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysDictTypeEntity::class)
class SysDictTypeVo : Serializable {
    /**
     * 字典主键
     */
    @field:ExcelProperty(value = ["字典主键"])
    var dictId: Long? = null

    /**
     * 字典名称
     */
    @field:ExcelProperty(value = ["字典名称"])
    var dictName: String? = null

    /**
     * 字典类型
     */
    @field:ExcelProperty(value = ["字典类型"])
    var dictType: String? = null

    /**
     * 备注
     */
    @field:ExcelProperty(value = ["备注"])
    var remark: String? = null

    /**
     * 创建时间
     */
    @field:ExcelProperty(value = ["创建时间"])
    var createTime: Date? = null
    
}
