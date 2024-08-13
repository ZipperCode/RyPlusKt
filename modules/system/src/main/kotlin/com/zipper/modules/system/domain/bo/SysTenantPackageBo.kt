package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysTenantPackageEntity
import io.github.linpeilie.annotations.AutoMapper
import io.github.linpeilie.annotations.AutoMapping
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.Data
import lombok.EqualsAndHashCode
import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import org.apache.commons.lang3.StringUtils

/**
 * 租户套餐业务对象 sys_tenant_package
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysTenantPackageEntity::class, reverseConvertGenerate = false)
class SysTenantPackageBo : BaseEntity() {
    /**
     * 租户套餐id
     */
    var packageId: @NotNull(message = "租户套餐id不能为空", groups = [EditGroup::class]) Long? = null

    /**
     * 套餐名称
     */
    var packageName: @NotBlank(message = "套餐名称不能为空", groups = [AddGroup::class, EditGroup::class]) String? = null

    /**
     * 关联菜单id
     */
    @field:AutoMapping(
        target = "menuIds",
        expression = "java(org.apache.commons.lang3.StringUtils.join(source.getMenuIds(), \",\"))"
    )
    var menuIds: Array<Long> = emptyArray()

    /**
     * 备注
     */
    var remark: String? = null

    /**
     * 菜单树选择项是否关联显示
     */
    var menuCheckStrictly: Boolean? = null

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null
}
