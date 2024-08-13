package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.mybatis.core.domain.BaseEntity
import lombok.Data
import lombok.EqualsAndHashCode
import org.apache.commons.lang3.StringUtils
import com.zipper.framework.core.constant.Constants
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.utils.StringUtilsExt

/**
 * 菜单权限表 sys_menu
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
class SysMenuEntity : BaseEntity() {
    /**
     * 菜单ID
     */
    @field:TableId(value = "menu_id")
    var menuId: Long? = null

    /**
     * 父菜单ID
     */
    var parentId: Long? = null

    /**
     * 菜单名称
     */
    var menuName: String? = null

    /**
     * 显示顺序
     */
    var orderNum: Int? = null

    /**
     * 路由地址
     */
    var path: String? = null

    /**
     * 组件路径
     */
    var component: String? = null

    /**
     * 路由参数
     */
    var queryParam: String? = null

    /**
     * 是否为外链（0是 1否）
     */
    var isFrame: String? = null

    fun setIsFrame(isFrame: String?) {
        this.isFrame = isFrame
    }
    fun getIsFrame(): String? = this.isFrame

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    var isCache: String? = null

    fun setIsCache(isCache: String?) {
        this.isCache = isCache
    }

    fun getIsCache(): String? = this.isCache

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    var menuType: String? = null

    /**
     * 显示状态（0显示 1隐藏）
     */
    var visible: String? = null

    /**
     * 菜单状态（0正常 1停用）
     */
    var status: String? = null

    /**
     * 权限字符串
     */
    var perms: String? = null

    /**
     * 菜单图标
     */
    var icon: String? = null

    /**
     * 备注
     */
    var remark: String? = null

    /**
     * 父菜单名称
     */
    @field:TableField(exist = false)
    var parentName: String? = null

    /**
     * 子菜单
     */
    @field:TableField(exist = false)
    var children: List<SysMenuEntity> = ArrayList()


    /**
     * 获取路由名称
     */
    fun getRouteName(): String {
        var routerName: String = StringUtils.capitalize(path)
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame()) {
            routerName = StringUtils.EMPTY
        }
        return routerName
    }

    /**
     * 获取路由地址
     */
    fun getRouterPath(): String? {
        var routerPath = this.path
        // 内链打开外网方式
        if (parentId != 0L && isInnerLink()) {
            routerPath = innerLinkReplaceEach(routerPath)
        }
        // 非外链并且是一级目录（类型为目录）
        if (0L == parentId && UserConstants.TYPE_DIR == menuType && UserConstants.NO_FRAME == isFrame) {
            routerPath = "/" + this.path
        } else if (isMenuFrame()) {
            routerPath = "/"
        }
        return routerPath
    }

    /**
     * 获取组件信息
     */
    fun getComponentInfo(): String? {
        var component: String? = UserConstants.LAYOUT
        if (StringUtils.isNotEmpty(this.component) && !isMenuFrame()) {
            component = this.component
        } else if (StringUtils.isEmpty(this.component) && parentId != 0L && isInnerLink()) {
            component = UserConstants.INNER_LINK
        } else if (StringUtils.isEmpty(this.component) && isParentView()) {
            component = UserConstants.PARENT_VIEW
        }
        return component
    }

    /**
     * 是否为菜单内部跳转
     */
    fun isMenuFrame(): Boolean {
        return parentId == 0L && UserConstants.TYPE_MENU == menuType && isFrame == UserConstants.NO_FRAME
    }

    /**
     * 是否为内链组件
     */
    fun isInnerLink(): Boolean {
        return isFrame == UserConstants.NO_FRAME && StringUtilsExt.ishttp(path)
    }

    /**
     * 是否为parent_view组件
     */
    fun isParentView(): Boolean {
        return parentId != 0L && UserConstants.TYPE_DIR == menuType
    }

    companion object {
        /**
         * 内链域名特殊字符替换
         */
        fun innerLinkReplaceEach(path: String?): String {
            return StringUtils.replaceEach(
                path,
                arrayOf(Constants.HTTP, Constants.HTTPS, Constants.WWW, ".", ":"),
                arrayOf("", "", "", "/", "/")
            )
        }
    }
}
