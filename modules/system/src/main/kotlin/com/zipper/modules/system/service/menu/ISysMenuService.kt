package com.zipper.modules.system.service.menu

import cn.hutool.core.lang.tree.Tree
import com.zipper.modules.system.domain.bo.SysMenuBo
import com.zipper.modules.system.domain.entity.SysMenuEntity
import com.zipper.modules.system.domain.vo.RouterVo
import com.zipper.modules.system.domain.vo.SysMenuVo

/**
 * 菜单 业务层
 *
 * @author Lion Li
 */
interface ISysMenuService {
    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    fun selectMenuList(userId: Long?): List<SysMenuVo>

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu   菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    fun selectMenuList(menu: SysMenuBo, userId: Long?): List<SysMenuVo>

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    fun selectMenuPermsByUserId(userId: Long?): Set<String>

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    fun selectMenuPermsByRoleId(roleId: Long?): Set<String>

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    fun selectMenuTreeByUserId(userId: Long?): List<SysMenuEntity>

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    fun selectMenuListByRoleId(roleId: Long?): List<Long>

    /**
     * 根据租户套餐ID查询菜单树信息
     *
     * @param packageId 租户套餐ID
     * @return 选中菜单列表
     */
    fun selectMenuListByPackageId(packageId: Long): List<Long>

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    fun buildMenus(menus: List<SysMenuEntity>): List<RouterVo>

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    fun buildMenuTreeSelect(menus: List<SysMenuVo>): List<Tree<Long>>

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    fun selectMenuById(menuId: Long?): SysMenuVo?

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    fun hasChildByMenuId(menuId: Long?): Boolean

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    fun checkMenuExistRole(menuId: Long?): Boolean

    /**
     * 新增保存菜单信息
     *
     * @param bo 菜单信息
     * @return 结果
     */
    fun insertMenu(bo: SysMenuBo): Int

    /**
     * 修改保存菜单信息
     *
     * @param bo 菜单信息
     * @return 结果
     */
    fun updateMenu(bo: SysMenuBo): Int

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    fun deleteMenuById(menuId: Long?): Int

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    fun checkMenuNameUnique(menu: SysMenuBo): Boolean
}
