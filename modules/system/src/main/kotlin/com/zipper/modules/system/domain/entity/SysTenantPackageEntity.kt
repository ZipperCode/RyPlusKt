package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.mybatis.core.domain.BaseEntity
import lombok.Data
import lombok.EqualsAndHashCode
import java.io.Serial

/**
 * 租户套餐对象 sys_tenant_package
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant_package")
class SysTenantPackageEntity : BaseEntity() {
    /**
     * 租户套餐id
     */
    @field:TableId(value = "package_id")
    var packageId: Long? = null

    /**
     * 套餐名称
     */
    var packageName: String? = null

    /**
     * 关联菜单id
     */
    var menuIds: String? = null

    /**
     * 备注
     */
    var remark: String? = null

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    var menuCheckStrictly: Boolean? = null

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @field:TableLogic
    var delFlag: String? = null

    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
