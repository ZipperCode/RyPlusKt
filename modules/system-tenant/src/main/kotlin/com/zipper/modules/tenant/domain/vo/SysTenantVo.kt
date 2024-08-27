package com.zipper.modules.tenant.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.framework.mybatis.core.domain.BaseMixinVo
import com.zipper.modules.tenant.domain.SysTenantMixin
import com.zipper.modules.tenant.domain.entity.SysTenantEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import lombok.EqualsAndHashCode
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.util.*

/**
 * 租户视图对象 sys_tenant
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
@AutoMapper(target = SysTenantEntity::class)
class SysTenantVo : BaseMixinVo(), SysTenantMixin {
    /**
     * id
     */
    @field:ExcelProperty(value = ["id"])
    override var id: Long? = null

    /**
     * 租户编号
     */
    @field:ExcelProperty(value = ["租户编号"])
    override var tenantId: String? = null

    /**
     * 联系人
     */
    @field:ExcelProperty(value = ["联系人"])
    override var contactUserName: String? = null

    /**
     * 联系电话
     */
    @field:ExcelProperty(value = ["联系电话"])
    override var contactPhone: String? = null

    /**
     * 企业名称
     */
    @field:ExcelProperty(value = ["企业名称"])
    override var companyName: String? = null

    /**
     * 统一社会信用代码
     */
    @field:ExcelProperty(value = ["统一社会信用代码"])
    override var licenseNumber: String? = null

    /**
     * 地址
     */
    @field:ExcelProperty(value = ["地址"])
    override var address: String? = null

    /**
     * 域名
     */
    @field:ExcelProperty(value = ["域名"])
    override var domain: String? = null

    /**
     * 企业简介
     */
    @field:ExcelProperty(value = ["企业简介"])
    override var intro: String? = null

    /**
     * 备注
     */
    @field:ExcelProperty(value = ["备注"])
    override var remark: String? = null

    /**
     * 租户套餐编号
     */
    @field:ExcelProperty(value = ["租户套餐编号"])
    override var packageId: Long? = null

    /**
     * 过期时间
     */
    @field:ExcelProperty(value = ["过期时间"])
    override var expireTime: Date? = null

    /**
     * 用户数量（-1不限制）
     */
    @field:ExcelProperty(value = ["用户数量"])
    override var accountCount: Long? = null

    /**
     * 租户状态（0正常 1停用）
     */
    @field:ExcelProperty(value = ["租户状态"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(readConverterExp = "0=正常,1=停用")
    override var status: String? = null


}
