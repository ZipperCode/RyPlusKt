package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.modules.system.domain.entity.SysLoginLogEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 系统访问记录视图对象 sys_logininfor
 *
 * @author Michelle.Chung
 * @date 2023-02-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysLoginLogEntity::class)
class SysLoginLogVo : Serializable {
    /**
     * 访问ID
     */
    @field:ExcelProperty(value = ["序号"])
    var infoId: Long? = null

    /**
     * 租户编号
     */
    var tenantId: String? = null

    /**
     * 用户账号
     */
    @field:ExcelProperty(value = ["用户账号"])
    var userName: String? = null

    /**
     * 客户端
     */
    @field:ExcelProperty(value = ["客户端"])
    var clientKey: String? = null

    /**
     * 设备类型
     */
    @field:ExcelProperty(value = ["设备类型"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_device_type")
    var deviceType: String? = null

    /**
     * 登录状态（0成功 1失败）
     */
    @field:ExcelProperty(value = ["登录状态"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_common_status")
    var status: String? = null

    /**
     * 登录IP地址
     */
    @field:ExcelProperty(value = ["登录地址"])
    var ipaddr: String? = null

    /**
     * 登录地点
     */
    @field:ExcelProperty(value = ["登录地点"])
    var loginLocation: String? = null

    /**
     * 浏览器类型
     */
    @field:ExcelProperty(value = ["浏览器"])
    var browser: String? = null

    /**
     * 操作系统
     */
    @field:ExcelProperty(value = ["操作系统"])
    var os: String? = null


    /**
     * 提示消息
     */
    @field:ExcelProperty(value = ["提示消息"])
    var msg: String? = null

    /**
     * 访问时间
     */
    @field:ExcelProperty(value = ["访问时间"])
    var loginTime: Date? = null

}
