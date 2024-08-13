package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelProperty
import lombok.Data
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serial
import java.io.Serializable

/**
 * 用户对象导入VO
 *
 * @author Lion Li
 */
@Data
class SysUserImportVo : Serializable {
    /**
     * 用户ID
     */
    @field:ExcelProperty(value = ["用户序号"])
    var userId: Long? = null

    /**
     * 部门ID
     */
    @field:ExcelProperty(value = ["部门编号"])
    var deptId: Long? = null

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
    @ExcelDictFormat(dictType = "sys_user_sex")
    var sex: String? = null

    /**
     * 帐号状态（0正常 1停用）
     */
    @field:ExcelProperty(value = ["帐号状态"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_normal_disable")
    var status: String? = null

}
