package com.zipper.modules.tenant.domain.param

import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import com.zipper.framework.mybatis.core.domain.BaseMixinVo
import com.zipper.modules.tenant.domain.SysTenantPackageMixin
import io.github.linpeilie.annotations.AutoMapping
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class SysTenantPackageSaveParam : BaseMixinVo(), SysTenantPackageMixin {

    /**
     * 租户套餐id
     */
    override var packageId: @NotNull(message = "租户套餐id不能为空", groups = [EditGroup::class]) Long? = null

    /**
     * 套餐名称
     */
    override var packageName: @NotBlank(message = "套餐名称不能为空", groups = [AddGroup::class, EditGroup::class]) String? = null

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
    override var remark: String? = null

    /**
     * 菜单树选择项是否关联显示
     */
    override var menuCheckStrictly: Boolean? = null

    /**
     * 状态（0正常 1停用）
     */
    override var status: String? = null
}