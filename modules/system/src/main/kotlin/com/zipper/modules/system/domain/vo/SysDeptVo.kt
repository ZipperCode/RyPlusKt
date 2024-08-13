package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.modules.system.domain.entity.SysDeptEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 部门视图对象 sys_dept
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysDeptEntity::class)
class SysDeptVo : Serializable {
    /**
     * 部门id
     */
    @field:ExcelProperty(value = ["部门id"])
    var deptId: Long? = null

    /**
     * 父部门id
     */
    var parentId: Long? = null

    /**
     * 父部门名称
     */
    var parentName: String? = null

    /**
     * 祖级列表
     */
    var ancestors: String? = null

    /**
     * 部门名称
     */
    @field:ExcelProperty(value = ["部门名称"])
    var deptName: String? = null

    /**
     * 显示顺序
     */
    var orderNum: Int? = null

    /**
     * 负责人ID
     */
    var leader: Long? = null

    /**
     * 负责人
     */
    @field:ExcelProperty(value = ["负责人"])
    var leaderName: String? = null

    /**
     * 联系电话
     */
    @field:ExcelProperty(value = ["联系电话"])
    var phone: String? = null

    /**
     * 邮箱
     */
    @field:ExcelProperty(value = ["邮箱"])
    var email: String? = null

    /**
     * 部门状态（0正常 1停用）
     */
    @field:ExcelProperty(value = ["部门状态"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_normal_disable")
    var status: String? = null

    /**
     * 创建时间
     */
    @field:ExcelProperty(value = ["创建时间"])
    var createTime: Date? = null
}
