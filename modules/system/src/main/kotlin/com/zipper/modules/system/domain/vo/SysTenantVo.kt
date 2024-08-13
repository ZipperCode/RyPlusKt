package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.modules.system.domain.entity.SysTenantEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 租户视图对象 sys_tenant
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysTenantEntity::class)
class SysTenantVo : Serializable {
    /**
     * id
     */
    @field:ExcelProperty(value = ["id"])
    var id: Long? = null

    /**
     * 租户编号
     */
    @field:ExcelProperty(value = ["租户编号"])
    var tenantId: String? = null

    /**
     * 联系人
     */
    @field:ExcelProperty(value = ["联系人"])
    var contactUserName: String? = null

    /**
     * 联系电话
     */
    @field:ExcelProperty(value = ["联系电话"])
    var contactPhone: String? = null

    /**
     * 企业名称
     */
    @field:ExcelProperty(value = ["企业名称"])
    var companyName: String? = null

    /**
     * 统一社会信用代码
     */
    @field:ExcelProperty(value = ["统一社会信用代码"])
    var licenseNumber: String? = null

    /**
     * 地址
     */
    @field:ExcelProperty(value = ["地址"])
    var address: String? = null

    /**
     * 域名
     */
    @field:ExcelProperty(value = ["域名"])
    var domain: String? = null

    /**
     * 企业简介
     */
    @field:ExcelProperty(value = ["企业简介"])
    var intro: String? = null

    /**
     * 备注
     */
    @field:ExcelProperty(value = ["备注"])
    var remark: String? = null

    /**
     * 租户套餐编号
     */
    @field:ExcelProperty(value = ["租户套餐编号"])
    var packageId: Long? = null

    /**
     * 过期时间
     */
    @field:ExcelProperty(value = ["过期时间"])
    var expireTime: Date? = null

    /**
     * 用户数量（-1不限制）
     */
    @field:ExcelProperty(value = ["用户数量"])
    var accountCount: Long? = null

    /**
     * 租户状态（0正常 1停用）
     */
    @field:ExcelProperty(value = ["租户状态"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(readConverterExp = "0=正常,1=停用")
    var status: String? = null


}
