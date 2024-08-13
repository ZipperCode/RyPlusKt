package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelProperty
import io.github.linpeilie.annotations.AutoMapper
import io.github.linpeilie.annotations.ReverseAutoMapping
import lombok.Data
import lombok.NoArgsConstructor
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 用户对象导出VO
 *
 * @author Lion Li
 */
@Data
@NoArgsConstructor
@AutoMapper(target = SysUserVo::class, convertGenerate = false)
class SysUserExportVo : Serializable {
    /**
     * 用户ID
     */
    @field:ExcelProperty(value = ["用户序号"])
    var userId: Long? = null

    /**
     * 用户账号
     */
    @field:ExcelProperty(value = ["登录名称"])
    var userName: String? = null

    /**
     * 用户昵称
     */
    @field:ExcelProperty(value = ["用户名称"])
    var nickName: String? = null

    /**
     * 用户邮箱
     */
    @field:ExcelProperty(value = ["用户邮箱"])
    var email: String? = null

    /**
     * 手机号码
     */
    @field:ExcelProperty(value = ["手机号码"])
    var phonenumber: String? = null

    /**
     * 用户性别
     */
    @field:ExcelProperty(value = ["用户性别"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_user_sex")
    var sex: String? = null

    /**
     * 帐号状态（0正常 1停用）
     */
    @field:ExcelProperty(value = ["帐号状态"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_normal_disable")
    var status: String? = null

    /**
     * 最后登录IP
     */
    @field:ExcelProperty(value = ["最后登录IP"])
    var loginIp: String? = null

    /**
     * 最后登录时间
     */
    @field:ExcelProperty(value = ["最后登录时间"])
    var loginDate: Date? = null

    /**
     * 部门名称
     */
    @ReverseAutoMapping(target = "deptName", source = "dept.deptName")
    @field:ExcelProperty(value = ["部门名称"])
    var deptName: String? = null

    /**
     * 负责人
     */
    @ReverseAutoMapping(target = "leaderName", source = "dept.leaderName")
    @field:ExcelProperty(value = ["部门负责人"])
    var leaderName: String? = null

}
