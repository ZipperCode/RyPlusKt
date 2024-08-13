package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.modules.system.domain.entity.SysDictDataEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 字典数据视图对象 sys_dict_data
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysDictDataEntity::class)
class SysDictDataVo : Serializable {
    /**
     * 字典编码
     */
    @field:ExcelProperty(value = ["字典编码"])
    var dictCode: Long? = null

    /**
     * 字典排序
     */
    @field:ExcelProperty(value = ["字典排序"])
    var dictSort: Int? = null

    /**
     * 字典标签
     */
    @field:ExcelProperty(value = ["字典标签"])
    var dictLabel: String = ""

    /**
     * 字典键值
     */
    @field:ExcelProperty(value = ["字典键值"])
    var dictValue: String = ""

    /**
     * 字典类型
     */
    @field:ExcelProperty(value = ["字典类型"])
    var dictType: String? = null

    /**
     * 样式属性（其他样式扩展）
     */
    var cssClass: String? = null

    /**
     * 表格回显样式
     */
    var listClass: String? = null

    /**
     * 是否默认（Y是 N否）
     */
    @field:ExcelProperty(value = ["是否默认"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_yes_no")
    var isDefault: String? = null

    fun setIsDefault(isDefault: String?) {
        this.isDefault = isDefault
    }
    fun getIsDefault(): String? = this.isDefault
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
