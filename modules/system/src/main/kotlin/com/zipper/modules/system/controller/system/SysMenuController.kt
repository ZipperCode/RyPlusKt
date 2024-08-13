package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import cn.hutool.core.lang.tree.Tree
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysMenuBo
import com.zipper.modules.system.domain.vo.MenuTreeSelectVo
import com.zipper.modules.system.domain.vo.RouterVo
import com.zipper.modules.system.domain.vo.SysMenuVo
import com.zipper.modules.system.service.menu.ISysMenuService
import org.apache.commons.lang3.StringUtils
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import com.zipper.framework.core.constant.TenantConstants
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.utils.StringUtilsExt

/**
 * 菜单信息
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/system/menu")
class SysMenuController(
    private val menuService: ISysMenuService
) : BaseController() {

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    fun getRouters(): R<List<RouterVo>> {
        val menus = menuService.selectMenuTreeByUserId(LoginHelper.getUserId())
        return R.ok(menuService.buildMenus(menus))
    }


    /**
     * 获取菜单列表
     */
    @SaCheckRole(
        value = [
            TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY
        ], mode = SaMode.OR
    )
    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    fun list(menu: SysMenuBo): R<List<SysMenuVo>> {
        val menus = menuService.selectMenuList(menu, LoginHelper.getUserId())
        return R.ok(menus)
    }

    /**
     * 根据菜单编号获取详细信息
     *
     * @param menuId 菜单ID
     */
    @SaCheckRole(
        value = [TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY
        ], mode = SaMode.OR
    )
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = ["/{menuId}"])
    fun getInfo(@PathVariable menuId: Long?): R<SysMenuVo?> {
        return R.ok(menuService.selectMenuById(menuId))
    }

    /**
     * 获取菜单下拉树列表
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping("/treeselect")
    fun treeselect(menu: SysMenuBo): R<List<Tree<Long>>> {
        val menus = menuService.selectMenuList(menu, LoginHelper.getUserId())
        return R.ok(menuService.buildMenuTreeSelect(menus))
    }

    /**
     * 加载对应角色菜单列表树
     *
     * @param roleId 角色ID
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = ["/roleMenuTreeselect/{roleId}"])
    fun roleMenuTreeselect(@PathVariable("roleId") roleId: Long?): R<MenuTreeSelectVo> {
        val menus = menuService.selectMenuList(LoginHelper.getUserId())
        val selectVo = MenuTreeSelectVo()
        selectVo.checkedKeys = menuService.selectMenuListByRoleId(roleId)
        selectVo.menus = menuService.buildMenuTreeSelect(menus)
        return R.ok(selectVo)
    }

    /**
     * 加载对应租户套餐菜单列表树
     *
     * @param packageId 租户套餐ID
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = ["/tenantPackageMenuTreeselect/{packageId}"])
    fun tenantPackageMenuTreeselect(@PathVariable("packageId") packageId: Long?): R<MenuTreeSelectVo> {
        val menus = menuService.selectMenuList(LoginHelper.getUserId())
        val selectVo = MenuTreeSelectVo()
        selectVo.checkedKeys = menuService.selectMenuListByPackageId(packageId)
        selectVo.menus = menuService.buildMenuTreeSelect(menus)
        return R.ok(selectVo)
    }

    /**
     * 新增菜单
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    fun add(@Validated @RequestBody menu: SysMenuBo): R<Void> {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.fail("新增菜单'" + menu.menuName + "'失败，菜单名称已存在")
        } else if (UserConstants.YES_FRAME == menu.isFrame && !StringUtilsExt.ishttp(menu.path)) {
            return R.fail("新增菜单'" + menu.menuName + "'失败，地址必须以http(s)://开头")
        }
        return toAjax(menuService.insertMenu(menu))
    }

    /**
     * 修改菜单
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    fun edit(@Validated @RequestBody menu: SysMenuBo): R<Void> {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.fail("修改菜单'" + menu.menuName + "'失败，菜单名称已存在")
        } else if (UserConstants.YES_FRAME == menu.isFrame && !StringUtilsExt.ishttp(menu.path)) {
            return R.fail("修改菜单'" + menu.menuName + "'失败，地址必须以http(s)://开头")
        } else if (menu.menuId == menu.parentId) {
            return R.fail("修改菜单'" + menu.menuName + "'失败，上级菜单不能选择自己")
        }
        return toAjax(menuService.updateMenu(menu))
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    fun remove(@PathVariable("menuId") menuId: Long?): R<Void> {
        if (menuService.hasChildByMenuId(menuId)) {
            return R.warn("存在子菜单,不允许删除")
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return R.warn("菜单已分配,不允许删除")
        }
        return toAjax(menuService.deleteMenuById(menuId))
    }
}
