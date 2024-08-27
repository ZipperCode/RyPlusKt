package com.zipper.modules.tenant.domain

import com.baomidou.mybatisplus.annotation.TableId
import com.zipper.framework.mybatis.core.domain.CreatorMixin
import com.zipper.framework.mybatis.core.domain.UpdaterMixin
import java.io.Serializable

interface SysTenantPackageMixin : CreatorMixin, UpdaterMixin, Serializable {

    /**
     * 租户套餐id
     */
    var packageId: Long? 

    /**
     * 套餐名称
     */
    var packageName: String? 

    /**
     * 关联菜单id
     */
//    var menuIds: String?

    /**
     * 备注
     */
    var remark: String? 

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    var menuCheckStrictly: Boolean? 

    /**
     * 状态（0正常 1停用）
     */
    var status: String? 
}