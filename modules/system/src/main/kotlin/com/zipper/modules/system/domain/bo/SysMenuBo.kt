package com.zipper.modules.system.domain.bo

import com.fasterxml.jackson.annotation.JsonInclude
import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysMenuEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 菜单权限业务对象 sys_menu
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysMenuEntity::class, reverseConvertGenerate = false)
class SysMenuBo : BaseEntity() {
    /**
     * 菜单ID
     */
    var menuId: Long? = null

    /**
     * 父菜单ID
     */
    var parentId: Long? = null

    /**
     * 菜单名称
     */
    @field:NotBlank(message = "菜单名称不能为空")
    @field:Size(
        min = 0,
        max = 50,
        message = "菜单名称长度不能超过{max}个字符"
    )
    var menuName: String? = null

    /**
     * 显示顺序
     */
    @field:NotNull(message = "显示顺序不能为空")
    var orderNum: Int? = null

    /**
     * 路由地址
     */
    @field:Size(min = 0, max = 200, message = "路由地址不能超过{max}个字符")
    var path: String? = null

    /**
     * 组件路径
     */
    @field:Size(min = 0, max = 200, message = "组件路径不能超过{max}个字符")
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
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @field:NotBlank(message = "菜单类型不能为空")
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
     * 权限标识
     */
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:Size(min = 0, max = 100, message = "权限标识长度不能超过{max}个字符")
    var perms: String? = null

    /**
     * 菜单图标
     */
    var icon: String? = null

    /**
     * 备注
     */
    var remark: String? = null
}
