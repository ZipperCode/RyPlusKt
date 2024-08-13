package com.zipper.modules.system.mapper

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.toolkit.Constants
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.system.domain.entity.SysMenuEntity
import com.zipper.modules.system.domain.vo.SysMenuVo
import org.apache.ibatis.annotations.Param
import com.zipper.framework.core.constant.UserConstants

/**
 * 菜单表 数据层
 *
 * @author Lion Li
 */
interface SysMenuMapper : BaseMapperPlusVo<SysMenuEntity, SysMenuVo> {
    /**
     * 根据用户所有权限
     *
     * @return 权限列表
     */
    fun selectMenuPerms(): List<String>

    /**
     * 根据用户查询系统菜单列表
     *
     * @param queryWrapper 查询条件
     * @return 菜单列表
     */
    fun selectMenuListByUserId(@Param(Constants.WRAPPER) queryWrapper: Wrapper<SysMenuEntity>): List<SysMenuEntity>

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    fun selectMenuPermsByUserId(userId: Long?): List<String>

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    fun selectMenuPermsByRoleId(roleId: Long?): List<String>

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    fun selectMenuTreeByUserId(userId: Long?): List<SysMenuEntity>

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId            角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    fun selectMenuListByRoleId(@Param("roleId") roleId: Long?, @Param("menuCheckStrictly") menuCheckStrictly: Boolean): List<Long>
}
