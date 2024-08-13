package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.modules.system.domain.entity.SysTenantPackageEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serial
import java.io.Serializable

/**
 * 租户套餐视图对象 sys_tenant_package
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysTenantPackageEntity::class)
class SysTenantPackageVo : Serializable {
    /**
     * 租户套餐id
     */
    @field:ExcelProperty(value = ["租户套餐id"])
    var packageId: Long? = null

    /**
     * 套餐名称
     */
    @field:ExcelProperty(value = ["套餐名称"])
    var packageName: String? = null

    /**
     * 关联菜单id
     */
    @field:ExcelProperty(value = ["关联菜单id"])
    var menuIds: String? = null

    /**
     * 备注
     */
    @field:ExcelProperty(value = ["备注"])
    var remark: String? = null

    /**
     * 菜单树选择项是否关联显示
     */
    @field:ExcelProperty(value = ["菜单树选择项是否关联显示"])
    var menuCheckStrictly: Boolean? = null

    /**
     * 状态（0正常 1停用）
     */
    @field:ExcelProperty(value = ["状态"], converter = ExcelDictConvert::class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=停用")
    var status: String? = null

}
