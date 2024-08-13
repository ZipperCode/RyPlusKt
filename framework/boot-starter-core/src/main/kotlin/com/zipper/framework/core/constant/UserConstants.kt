package com.zipper.framework.core.constant

/**
 * 用户常量信息
 *
 * @author ruoyi
 */
object UserConstants {
    /**
     * 平台内系统用户的唯一标志
     */
    const val SYS_USER: String = "SYS_USER"

    /**
     * 正常状态
     */
    const val NORMAL: String = "0"

    /**
     * 异常状态
     */
    const val EXCEPTION: String = "1"

    /**
     * 用户正常状态
     */
    const val USER_NORMAL: String = "0"

    /**
     * 用户封禁状态
     */
    const val USER_DISABLE: String = "1"

    /**
     * 角色正常状态
     */
    const val ROLE_NORMAL: String = "0"

    /**
     * 角色封禁状态
     */
    const val ROLE_DISABLE: String = "1"

    /**
     * 部门正常状态
     */
    const val DEPT_NORMAL: String = "0"

    /**
     * 部门停用状态
     */
    const val DEPT_DISABLE: String = "1"

    /**
     * 岗位正常状态
     */
    const val POST_NORMAL: String = "0"

    /**
     * 岗位停用状态
     */
    const val POST_DISABLE: String = "1"

    /**
     * 字典正常状态
     */
    const val DICT_NORMAL: String = "0"

    /**
     * 是否为系统默认（是）
     */
    const val YES: String = "Y"

    /**
     * 是否菜单外链（是）
     */
    const val YES_FRAME: String = "0"

    /**
     * 是否菜单外链（否）
     */
    const val NO_FRAME: String = "1"

    /**
     * 菜单正常状态
     */
    const val MENU_NORMAL: String = "0"

    /**
     * 菜单停用状态
     */
    const val MENU_DISABLE: String = "1"

    /**
     * 菜单类型（目录）
     */
    const val TYPE_DIR: String = "M"

    /**
     * 菜单类型（菜单）
     */
    const val TYPE_MENU: String = "C"

    /**
     * 菜单类型（按钮）
     */
    const val TYPE_BUTTON: String = "F"

    /**
     * Layout组件标识
     */
    const val LAYOUT: String = "Layout"

    /**
     * ParentView组件标识
     */
    const val PARENT_VIEW: String = "ParentView"

    /**
     * InnerLink组件标识
     */
    const val INNER_LINK: String = "InnerLink"

    /**
     * 用户名长度限制
     */
    const val USERNAME_MIN_LENGTH: Int = 2
    const val USERNAME_MAX_LENGTH: Int = 20

    /**
     * 密码长度限制
     */
    const val PASSWORD_MIN_LENGTH: Int = 5
    const val PASSWORD_MAX_LENGTH: Int = 20

    /**
     * 超级管理员ID
     */
    const val SUPER_ADMIN_ID: Long = 1L

}
