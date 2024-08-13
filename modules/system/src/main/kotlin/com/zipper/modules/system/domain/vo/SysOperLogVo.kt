package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.modules.system.domain.entity.SysOperLogEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 操作日志记录视图对象 sys_oper_log
 *
 * @author Michelle.Chung
 * @date 2023-02-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysOperLogEntity::class)
class SysOperLogVo : Serializable {
    /**
     * 日志主键
     */
    @field:ExcelProperty(value = ["日志主键"])
    var operId: Long? = null

    /**
     * 租户编号
     */
    var tenantId: String? = null

    /**
     * 模块标题
     */
    @field:ExcelProperty(value = ["操作模块"])
    var title: String? = null

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @field:ExcelProperty(value = ["业务类型"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_oper_type")
    var businessType: Int? = null

    /**
     * 业务类型数组
     */
    var businessTypes: Array<Int> = emptyArray()

    /**
     * 方法名称
     */
    @field:ExcelProperty(value = ["请求方法"])
    var method: String? = null

    /**
     * 请求方式
     */
    @field:ExcelProperty(value = ["请求方式"])
    var requestMethod: String? = null

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @field:ExcelProperty(value = ["操作类别"], converter = ExcelDictConvert::class)
    @ExcelDictFormat(readConverterExp = "0=其它,1=后台用户,2=手机端用户")
    var operatorType: Int? = null

    /**
     * 操作人员
     */
    @field:ExcelProperty(value = ["操作人员"])
    var operName: String? = null

    /**
     * 部门名称
     */
    @field:ExcelProperty(value = ["部门名称"])
    var deptName: String? = null

    /**
     * 请求URL
     */
    @field:ExcelProperty(value = ["请求地址"])
    var operUrl: String? = null

    /**
     * 主机地址
     */
    @field:ExcelProperty(value = ["操作地址"])
    var operIp: String? = null

    /**
     * 操作地点
     */
    @field:ExcelProperty(value = ["操作地点"])
    var operLocation: String? = null

    /**
     * 请求参数
     */
    @field:ExcelProperty(value = ["请求参数"])
    var operParam: String? = null

    /**
     * 返回参数
     */
    @field:ExcelProperty(value = ["返回参数"])
    var jsonResult: String? = null

    /**
     * 操作状态（0正常 1异常）
     */
    @field:ExcelProperty(value = ["状态"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_common_status")
    var status: Int? = null

    /**
     * 错误消息
     */
    @field:ExcelProperty(value = ["错误消息"])
    var errorMsg: String? = null

    /**
     * 操作时间
     */
    @field:ExcelProperty(value = ["操作时间"])
    var operTime: Date? = null

    /**
     * 消耗时间
     */
    @field:ExcelProperty(value = ["消耗时间"])
    var costTime: Long? = null

}
