package com.zipper.modules.tenant.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.framework.mybatis.core.domain.BaseMixinVo
import com.zipper.modules.tenant.domain.SysTenantPackageMixin
import com.zipper.modules.tenant.domain.entity.SysTenantPackageEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import lombok.EqualsAndHashCode
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert

/**
 * 租户套餐视图对象 sys_tenant_package
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysTenantPackageEntity::class)
class SysTenantPackageVo : BaseMixinVo(), SysTenantPackageMixin {
    /**
     * 租户套餐id
     */
    @field:ExcelProperty(value = ["租户套餐id"])
    override var packageId: Long? = null

    /**
     * 套餐名称
     */
    @field:ExcelProperty(value = ["套餐名称"])
    override var packageName: String? = null

    /**
     * 关联菜单id
     */
    @field:ExcelProperty(value = ["关联菜单id"])
    var menuIds: String? = null

    /**
     * 备注
     */
    @field:ExcelProperty(value = ["备注"])
    override var remark: String? = null

    /**
     * 菜单树选择项是否关联显示
     */
    @field:ExcelProperty(value = ["菜单树选择项是否关联显示"])
    override var menuCheckStrictly: Boolean? = null

    /**
     * 状态（0正常 1停用）
     */
    @field:ExcelProperty(value = ["状态"], converter = ExcelDictConvert::class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=停用")
    override var status: String? = null
}
