package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.modules.system.domain.entity.SysPostEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 岗位信息视图对象 sys_post
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysPostEntity::class)
class SysPostVo : Serializable {
    /**
     * 岗位ID
     */
    @field:ExcelProperty(value = ["岗位序号"])
    var postId: Long? = null

    /**
     * 岗位编码
     */
    @field:ExcelProperty(value = ["岗位编码"])
    var postCode: String? = null

    /**
     * 岗位名称
     */
    @field:ExcelProperty(value = ["岗位名称"])
    var postName: String? = null

    /**
     * 显示顺序
     */
    @field:ExcelProperty(value = ["岗位排序"])
    var postSort: Int? = null

    /**
     * 状态（0正常 1停用）
     */
    @field:ExcelProperty(value = ["状态"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_normal_disable")
    var status: String? = null

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
