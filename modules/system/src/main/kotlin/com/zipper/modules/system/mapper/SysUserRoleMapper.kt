package com.zipper.modules.system.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.modules.system.domain.entity.SysUserRoleEntity


/**
 * 用户与角色关联表 数据层
 *
 * @author Lion Li
 */
interface SysUserRoleMapper : BaseMapperPlus<SysUserRoleEntity> {
    fun selectUserIdsByRoleId(roleId: Long?): List<Long>
}
