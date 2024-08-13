package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.modules.system.domain.entity.SysConfigEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serializable
import java.util.*

/**
 * 参数配置视图对象 sys_config
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysConfigEntity::class)
class SysConfigVo : Serializable {
    /**
     * 参数主键
     */
    @field:ExcelProperty(value = ["参数主键"])
    var configId: Long? = null

    /**
     * 参数名称
     */
    @field:ExcelProperty(value = ["参数名称"])
    var configName: String? = null

    /**
     * 参数键名
     */
    @field:ExcelProperty(value = ["参数键名"])
    var configKey: String? = null

    /**
     * 参数键值
     */
    @field:ExcelProperty(value = ["参数键值"])
    var configValue: String? = null

    /**
     * 系统内置（Y是 N否）
     */
    @field:ExcelProperty(value = ["系统内置"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_yes_no")
    var configType: String? = null

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
