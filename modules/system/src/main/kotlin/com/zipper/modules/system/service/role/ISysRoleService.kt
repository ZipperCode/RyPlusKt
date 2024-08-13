package com.zipper.modules.system.service.role

import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysRoleBo
import com.zipper.modules.system.domain.entity.SysUserRoleEntity
import com.zipper.modules.system.domain.vo.SysRoleVo

/**
 * 角色业务层
 *
 * @author Lion Li
 */
interface ISysRoleService {
    fun selectPageRoleList(role: SysRoleBo, pageQuery: PageQuery): TableDataInfo<SysRoleVo>

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    fun selectRoleList(role: SysRoleBo): List<SysRoleVo>

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    fun selectRolesByUserId(userId: Long?): List<SysRoleVo>

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    fun selectRolePermissionByUserId(userId: Long): Set<String>

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    fun selectRoleAll(): List<SysRoleVo>

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    fun selectRoleListByUserId(userId: Long?): List<Long>

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    fun selectRoleById(roleId: Long?): SysRoleVo?

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    fun checkRoleNameUnique(role: SysRoleBo): Boolean

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    fun checkRoleKeyUnique(role: SysRoleBo): Boolean

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    fun checkRoleAllowed(role: SysRoleBo)

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    fun checkRoleDataScope(roleId: Long?)

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    fun countUserRoleByRoleId(roleId: Long?): Long

    /**
     * 新增保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    fun insertRole(bo: SysRoleBo): Int

    /**
     * 修改保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    fun updateRole(bo: SysRoleBo): Int

    /**
     * 修改角色状态
     *
     * @param roleId 角色ID
     * @param status 角色状态
     * @return 结果
     */
    fun updateRoleStatus(roleId: Long?, status: String?): Int

    /**
     * 修改数据权限信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    fun authDataScope(bo: SysRoleBo): Int

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    fun deleteRoleById(roleId: Long?): Int

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    fun deleteRoleByIds(roleIds: Array<Long>): Int

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    fun deleteAuthUser(userRole: SysUserRoleEntity): Int

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    fun deleteAuthUsers(roleId: Long?, userIds: Array<Long>): Int

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    fun insertAuthUsers(roleId: Long?, userIds: Array<Long>): Int

    fun cleanOnlineUserByRole(roleId: Long?)
}
