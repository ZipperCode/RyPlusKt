package com.zipper.modules.system.mapper

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.toolkit.Constants
import com.zipper.framework.mybatis.annotation.DataColumn
import com.zipper.framework.mybatis.annotation.DataPermission
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.system.domain.entity.SysDeptEntity
import com.zipper.modules.system.domain.vo.SysDeptVo
import org.apache.ibatis.annotations.Param

/**
 * 部门管理 数据层
 *
 * @author Lion Li
 */
interface SysDeptMapper : BaseMapperPlusVo<SysDeptEntity, SysDeptVo> {
    /**
     * 查询部门管理数据
     *
     * @param queryWrapper 查询条件
     * @return 部门信息集合
     */
    @DataPermission(DataColumn(key = ["deptName"], value = ["dept_id"]))
    fun selectDeptList(@Param(Constants.WRAPPER) queryWrapper: Wrapper<SysDeptEntity>?): List<SysDeptVo>

    @DataPermission(DataColumn(key = ["deptName"], value = ["dept_id"]))
    fun selectDeptById(deptId: Long?): SysDeptVo?

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId            角色ID
     * @param deptCheckStrictly 部门树选择项是否关联显示
     * @return 选中部门列表
     */
    fun selectDeptListByRoleId(
        @Param("roleId") roleId: Long?,
        @Param("deptCheckStrictly") deptCheckStrictly: Boolean?
    ): List<Long>
}
