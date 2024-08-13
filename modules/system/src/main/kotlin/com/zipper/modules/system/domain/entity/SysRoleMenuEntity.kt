package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import lombok.Data

/**
 * 角色和菜单关联 sys_role_menu
 *
 * @author Lion Li
 */
@Data
@TableName("sys_role_menu")
class SysRoleMenuEntity {
    /**
     * 角色ID
     */
    @field:TableId(type = IdType.INPUT)
    var roleId: Long? = null

    /**
     * 菜单ID
     */
    var menuId: Long? = null
}
