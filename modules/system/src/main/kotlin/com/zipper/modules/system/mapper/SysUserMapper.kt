package com.zipper.modules.system.mapper

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.toolkit.Constants
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.mybatis.annotation.DataColumn
import com.zipper.framework.mybatis.annotation.DataPermission
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.system.domain.entity.SysUserEntity
import com.zipper.modules.system.domain.vo.SysUserVo
import org.apache.ibatis.annotations.Param

/**
 * 用户表 数据层
 *
 * @author Lion Li
 */
interface SysUserMapper : BaseMapperPlusVo<SysUserEntity, SysUserVo> {
    @DataPermission(
        DataColumn(key = ["deptName"], value = ["d.dept_id"]),
        DataColumn(key = ["userName"], value = ["u.user_id"])
    )
    fun selectPageUserList(
        @Param("page") page: Page<SysUserEntity>,
        @Param(Constants.WRAPPER) queryWrapper: Wrapper<SysUserEntity>
    ): Page<SysUserVo>

    /**
     * 根据条件分页查询用户列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission(
        DataColumn(key = ["deptName"], value = ["d.dept_id"]),
        DataColumn(key = ["userName"], value = ["u.user_id"])
    )
    fun selectUserList(
        @Param(Constants.WRAPPER) queryWrapper: Wrapper<SysUserEntity>
    ): List<SysUserVo>

    /**
     * 根据条件分页查询已配用户角色列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission(
        DataColumn(key = ["deptName"], value = ["d.dept_id"]),
        DataColumn(key = ["userName"], value = ["u.user_id"])
    )
    fun selectAllocatedList(
        @Param("page") page: Page<SysUserEntity>, @Param(Constants.WRAPPER) queryWrapper: Wrapper<SysUserEntity>
    ): Page<SysUserVo>

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission(
        DataColumn(key = ["deptName"], value = ["d.dept_id"]),
        DataColumn(key = ["userName"], value = ["u.user_id"])
    )
    fun selectUnallocatedList(
        @Param("page") page: Page<SysUserEntity>, @Param(
            Constants.WRAPPER
        ) queryWrapper: Wrapper<SysUserEntity>
    ): Page<SysUserVo>

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    fun selectUserByUserName(userName: String?): SysUserVo?

    /**
     * 通过手机号查询用户
     *
     * @param phonenumber 手机号
     * @return 用户对象信息
     */
    fun selectUserByPhonenumber(phonenumber: String?): SysUserVo?

    /**
     * 通过邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象信息
     */
    fun selectUserByEmail(email: String?): SysUserVo?

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @DataPermission(
        DataColumn(key = ["deptName"], value = ["d.dept_id"]),
        DataColumn(key = ["userName"], value = ["u.user_id"])
    )
    fun selectUserById(userId: Long?): SysUserVo?

    @DataPermission(
        DataColumn(key = ["deptName"], value = ["dept_id"]),
        DataColumn(key = ["userName"], value = ["user_id"])
    )
    override fun update(
        @Param(Constants.ENTITY) user: SysUserEntity?,
        @Param(Constants.WRAPPER) updateWrapper: Wrapper<SysUserEntity>
    ): Int

    @DataPermission(
        DataColumn(key = ["deptName"], value = ["dept_id"]),
        DataColumn(key = ["userName"], value = ["user_id"])
    )
    override fun updateById(
        @Param(Constants.ENTITY) user: SysUserEntity
    ): Int
}
