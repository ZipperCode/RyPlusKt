package com.zipper.modules.system.service.menu

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.lang.tree.Tree
import cn.hutool.core.util.ObjectUtil
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.modules.system.domain.bo.SysMenuBo
import com.zipper.modules.system.domain.entity.SysMenuEntity
import com.zipper.modules.system.domain.entity.SysRoleMenuEntity
import com.zipper.modules.system.domain.vo.MetaVo
import com.zipper.modules.system.domain.vo.RouterVo
import com.zipper.modules.system.domain.vo.SysMenuVo
import com.zipper.modules.system.mapper.SysMenuMapper
import com.zipper.modules.system.mapper.SysRoleMapper
import com.zipper.modules.system.mapper.SysRoleMenuMapper
import com.zipper.modules.system.mapper.SysTenantPackageMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.framework.core.utils.MapstructUtils.convertWithClass
import com.zipper.framework.core.utils.StringUtilsExt
import com.zipper.framework.core.utils.TreeBuildUtils
import java.util.*
import java.util.function.Function

/**
 * 菜单 业务层处理
 *
 * @author Lion Li
 */
@Service
class SysMenuServiceImpl(
    private val baseMapper: SysMenuMapper,
    private val roleMapper: SysRoleMapper,
    private val roleMenuMapper: SysRoleMenuMapper,
    private val tenantPackageMapper: SysTenantPackageMapper
) : ISysMenuService {
    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    override fun selectMenuList(userId: Long?): List<SysMenuVo> {
        return selectMenuList(SysMenuBo(), userId)
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    override fun selectMenuList(menu: SysMenuBo, userId: Long?): List<SysMenuVo> {
        val menuList: List<SysMenuVo>
        // 管理员显示所有菜单信息
        if (LoginHelper.isSuperAdmin(userId)) {
            menuList = baseMapper.selectVoList(
                MybatisKt.ktQuery<SysMenuEntity>()
                    .like(StringUtils.isNotBlank(menu.menuName), SysMenuEntity::menuName, menu.menuName)
                    .eq(StringUtils.isNotBlank(menu.visible), SysMenuEntity::visible, menu.visible)
                    .eq(StringUtils.isNotBlank(menu.status), SysMenuEntity::status, menu.status)
                    .orderByAsc(SysMenuEntity::parentId)
                    .orderByAsc(SysMenuEntity::orderNum)
            )
        } else {
            val wrapper = Wrappers.query<SysMenuEntity>()
            wrapper.eq("sur.user_id", userId)
                .like(StringUtils.isNotBlank(menu.menuName), "m.menu_name", menu.menuName)
                .eq(StringUtils.isNotBlank(menu.visible), "m.visible", menu.visible)
                .eq(StringUtils.isNotBlank(menu.status), "m.status", menu.status)
                .orderByAsc("m.parent_id")
                .orderByAsc("m.order_num")
            val list = baseMapper.selectMenuListByUserId(wrapper)
            menuList = convertWithClass<SysMenuEntity, SysMenuVo>(list, SysMenuVo::class.java)
        }
        return menuList
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    override fun selectMenuPermsByUserId(userId: Long?): Set<String> {
        val perms = baseMapper.selectMenuPermsByUserId(userId)
        val permsSet: MutableSet<String> = HashSet()
        for (perm in perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(StringUtilsExt.splitList(perm.trim { it <= ' ' }))
            }
        }
        return permsSet
    }

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    override fun selectMenuPermsByRoleId(roleId: Long?): Set<String> {
        val perms = baseMapper.selectMenuPermsByRoleId(roleId)
        val permsSet: MutableSet<String> = HashSet()
        for (perm in perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(StringUtilsExt.splitList(perm.trim { it <= ' ' }))
            }
        }
        return permsSet
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    override fun selectMenuTreeByUserId(userId: Long?): List<SysMenuEntity> {
        val menus = if (LoginHelper.isSuperAdmin(userId)) {
            val lqw = MybatisKt.ktQuery<SysMenuEntity>()
                .`in`(SysMenuEntity::menuType, UserConstants.TYPE_DIR, UserConstants.TYPE_MENU)
                .eq(SysMenuEntity::status, UserConstants.MENU_NORMAL)
                .orderByAsc(SysMenuEntity::parentId)
                .orderByAsc(SysMenuEntity::orderNum)
            baseMapper.selectList(lqw)
        } else {
            baseMapper.selectMenuTreeByUserId(userId)
        }
        return getChildPerms(menus, 0)
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    override fun selectMenuListByRoleId(roleId: Long?): List<Long> {
        val role = roleMapper.selectById(roleId)
        return baseMapper.selectMenuListByRoleId(roleId, role.menuCheckStrictly!!)
    }

    /**
     * 根据租户套餐ID查询菜单树信息
     *
     * @param packageId 租户套餐ID
     * @return 选中菜单列表
     */
    override fun selectMenuListByPackageId(packageId: Long?): List<Long> {
        val tenantPackage = tenantPackageMapper.selectById(packageId)
        val menuIds: List<Long?> = StringUtilsExt.splitTo(tenantPackage.menuIds, Convert::toLong)
        if (CollUtil.isEmpty(menuIds)) {
            return listOf()
        }
        var parentIds: List<Long?>? = null
        if (tenantPackage.menuCheckStrictly!!) {
            parentIds = baseMapper.selectObjs(MybatisKt.ktQuery<SysMenuEntity>()
                .select(SysMenuEntity::parentId)
                .`in`(SysMenuEntity::menuId, menuIds), Function { x: Any? -> Convert.toLong(x) })
        }
        return baseMapper.selectObjs(MybatisKt.ktQuery<SysMenuEntity>()
            .`in`(SysMenuEntity::menuId, menuIds)
            .notIn(CollUtil.isNotEmpty(parentIds), SysMenuEntity::menuId, parentIds), Function { x: Any? -> Convert.toLong(x) })
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    override fun buildMenus(menus: List<SysMenuEntity>): List<RouterVo> {
        val routers: MutableList<RouterVo> = LinkedList()
        for (menu in menus) {
            val router = RouterVo()
            router.hidden = "1" == menu.visible
            router.name = menu.getRouteName()
            router.path = menu.getRouterPath()
            router.component = menu.getComponentInfo()
            router.query = menu.queryParam
            router.meta = MetaVo(menu.menuName!!, menu.icon!!, StringUtils.equals("1", menu.getIsCache()), menu.path)
            val cMenus = menu.children
            if (CollUtil.isNotEmpty(cMenus) && UserConstants.TYPE_DIR == menu.menuType) {
                router.alwaysShow = true
                router.redirect = "noRedirect"
                router.children = buildMenus(cMenus)
            } else if (menu.isMenuFrame()) {
                router.meta = null
                val childrenList: MutableList<RouterVo> = ArrayList()
                val children = RouterVo()
                children.path = menu.path
                children.component = menu.component
                children.name = StringUtils.capitalize(menu.path)
                children.meta = MetaVo(menu.menuName!!, menu.icon!!, StringUtils.equals("1", menu.isCache), menu.path)
                children.query = menu.queryParam
                childrenList.add(children)
                router.children = childrenList
            } else if (menu.parentId!!.toInt() == 0 && menu.isInnerLink()) {
                router.meta = MetaVo(menu.menuName!!, menu.icon!!)
                router.path = "/"
                val childrenList: MutableList<RouterVo> = ArrayList()
                val children = RouterVo()
                val routerPath = SysMenuEntity.innerLinkReplaceEach(menu.path)
                children.path = routerPath
                children.component = UserConstants.INNER_LINK
                children.name = StringUtils.capitalize(routerPath)
                children.meta = MetaVo(menu.menuName!!, menu.icon!!, menu.path)
                childrenList.add(children)
                router.children = childrenList
            }
            routers.add(router)
        }
        return routers
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    override fun buildMenuTreeSelect(menus: List<SysMenuVo>): List<Tree<Long>> {
        if (CollUtil.isEmpty(menus)) {
            return CollUtil.newArrayList()
        }
        return TreeBuildUtils.build(menus) { menu, tree ->
            tree.setId(menu.menuId)
                .setParentId(menu.parentId)
                .setName(menu.menuName)
                .setWeight(menu.orderNum)
        }
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    override fun selectMenuById(menuId: Long?): SysMenuVo? {
        return baseMapper.selectVoById(menuId)
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    override fun hasChildByMenuId(menuId: Long?): Boolean {
        return baseMapper.exists(MybatisKt.ktQuery<SysMenuEntity>().eq(SysMenuEntity::parentId, menuId))
    }

    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    override fun checkMenuExistRole(menuId: Long?): Boolean {
        return roleMenuMapper.exists(MybatisKt.ktQuery<SysRoleMenuEntity>().eq(SysRoleMenuEntity::menuId, menuId))
    }

    /**
     * 新增保存菜单信息
     *
     * @param bo 菜单信息
     * @return 结果
     */
    override fun insertMenu(bo: SysMenuBo): Int {
        val menu = convert(bo, SysMenuEntity::class.java)
        return baseMapper.insert(menu)
    }

    /**
     * 修改保存菜单信息
     *
     * @param bo 菜单信息
     * @return 结果
     */
    override fun updateMenu(bo: SysMenuBo): Int {
        val menu = convert(bo, SysMenuEntity::class.java)
        return baseMapper.updateById(menu)
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    override fun deleteMenuById(menuId: Long?): Int {
        return baseMapper.deleteById(menuId)
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    override fun checkMenuNameUnique(menu: SysMenuBo): Boolean {
        val exist = baseMapper.exists(
            MybatisKt.ktQuery<SysMenuEntity>()
                .eq(SysMenuEntity::menuName, menu.menuName)
                .eq(SysMenuEntity::parentId, menu.parentId)
                .ne(ObjectUtil.isNotNull(menu.menuId), SysMenuEntity::menuId, menu.menuId)
        )
        return !exist
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    private fun getChildPerms(list: List<SysMenuEntity>, parentId: Int): List<SysMenuEntity> {
        val returnList: MutableList<SysMenuEntity> = ArrayList()
        for (t in list) {
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.parentId == parentId.toLong()) {
                recursionFn(list, t)
                returnList.add(t)
            }
        }
        return returnList
    }

    /**
     * 递归列表
     */
    private fun recursionFn(list: List<SysMenuEntity>, t: SysMenuEntity) {
        // 得到子节点列表
        t.children = list.filter { it.parentId == t.menuId }
        for (tChild in t.children) {
            // 判断是否有子节点
            if (list.any { it.parentId == tChild.menuId }) {
                recursionFn(list, tChild)
            }
        }
    }
}
