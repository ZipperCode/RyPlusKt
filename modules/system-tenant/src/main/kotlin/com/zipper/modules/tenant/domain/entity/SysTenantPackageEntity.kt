package com.zipper.modules.tenant.domain.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.mybatis.core.domain.BaseMixinEntity
import com.zipper.modules.tenant.domain.SysTenantPackageMixin
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 租户套餐对象 sys_tenant_package
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant_package")
class SysTenantPackageEntity : BaseMixinEntity(), SysTenantPackageMixin {
    /**
     * 租户套餐id
     */
    @field:TableId(value = "package_id", type = IdType.AUTO)
    override var packageId: Long? = null

    val requirePackageId get() = packageId!!

    /**
     * 套餐名称
     */
    override var packageName: String? = null

    /**
     * 关联菜单id
     */
    var menuIds: String? = null

    /**
     * 备注
     */
    override var remark: String? = null

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    override var menuCheckStrictly: Boolean? = null

    /**
     * 状态（0正常 1停用）
     */
    override var status: String? = null
}
