package com.zipper.modules.system.domain.vo

import lombok.Data

/**
 * 用户信息
 *
 * @author Michelle.Chung
 */
@Data
class SysUserInfoVo {
    /**
     * 用户信息
     */
    var user: SysUserVo? = null

    /**
     * 角色ID列表
     */
    var roleIds: List<Long>? = null

    /**
     * 角色列表
     */
    var roles: List<SysRoleVo>? = null

    /**
     * 岗位ID列表
     */
    var postIds: List<Long>? = null

    /**
     * 岗位列表
     */
    var posts: List<SysPostVo>? = null
}
