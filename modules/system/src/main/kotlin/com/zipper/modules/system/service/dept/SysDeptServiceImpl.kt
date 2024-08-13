package com.zipper.modules.system.service.dept

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.lang.tree.Tree
import cn.hutool.core.util.ObjectUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.helper.DataBaseHelper
import com.zipper.framework.redis.utils.CacheUtils
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.modules.system.domain.bo.SysDeptBo
import com.zipper.modules.system.domain.entity.SysDeptEntity
import com.zipper.modules.system.domain.entity.SysUserEntity
import com.zipper.modules.system.domain.vo.SysDeptVo
import com.zipper.modules.system.mapper.SysDeptMapper
import com.zipper.modules.system.mapper.SysRoleMapper
import com.zipper.modules.system.mapper.SysUserMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import com.zipper.framework.core.constant.CacheNames
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.service.DeptService
import com.zipper.framework.core.utils.MapstructUtils
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.framework.core.utils.StringUtilsExt
import com.zipper.framework.core.utils.TreeBuildUtils
import java.util.*

/**
 * 部门管理 服务实现
 *
 * @author Lion Li
 */

@Service
class SysDeptServiceImpl(
    private val baseMapper: SysDeptMapper,
    private val roleMapper: SysRoleMapper,
    private val userMapper: SysUserMapper,
) : ISysDeptService, DeptService {


    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    override fun selectDeptList(dept: SysDeptBo): List<SysDeptVo> {
        val lqw = buildQueryWrapper(dept)
        return baseMapper.selectDeptList(lqw)
    }

    /**
     * 查询部门树结构信息
     *
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    override fun selectDeptTreeList(dept: SysDeptBo): List<Tree<Long>> {
        // 只查询未禁用部门
        dept.status = UserConstants.DEPT_NORMAL
        val lqw = buildQueryWrapper(dept)
        val depts = baseMapper.selectDeptList(lqw)
        return buildDeptTreeSelect(depts)
    }

    private fun buildQueryWrapper(bo: SysDeptBo): KtQueryWrapper<SysDeptEntity> {
        val lqw = MybatisKt.ktQuery<SysDeptEntity>()
        lqw.eq(SysDeptEntity::delFlag, "0")
        lqw.eq(ObjectUtil.isNotNull(bo.deptId), SysDeptEntity::deptId, bo.deptId)
        lqw.eq(ObjectUtil.isNotNull(bo.parentId), SysDeptEntity::parentId, bo.parentId)
        lqw.like(StringUtils.isNotBlank(bo.deptName), SysDeptEntity::deptName, bo.deptName)
        lqw.eq(StringUtils.isNotBlank(bo.status), SysDeptEntity::status, bo.status)
        lqw.orderByAsc(SysDeptEntity::parentId)
        lqw.orderByAsc(SysDeptEntity::orderNum)
        lqw.orderByAsc(SysDeptEntity::deptId)
        return lqw
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    override fun buildDeptTreeSelect(depts: List<SysDeptVo>): List<Tree<Long>> {
        if (CollUtil.isEmpty(depts)) {
            return CollUtil.newArrayList()
        }
        return TreeBuildUtils.build(depts) { dept, tree ->
            tree.setId(dept.deptId)
                .setParentId(dept.parentId)
                .setName(dept.deptName)
                .setWeight(dept.orderNum)
        }
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    override fun selectDeptListByRoleId(roleId: Long?): List<Long> {
        val role = roleMapper.selectById(roleId)
        return baseMapper.selectDeptListByRoleId(roleId, role.deptCheckStrictly)
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Cacheable(cacheNames = [CacheNames.SYS_DEPT], key = "#deptId")
    override fun selectDeptById(deptId: Long?): SysDeptVo? {
        val dept = baseMapper.selectVoById(deptId) ?: return null
        val parentDept = baseMapper.selectVoOne(
            MybatisKt.ktQuery<SysDeptEntity>()
                .select(SysDeptEntity::deptName).eq(SysDeptEntity::deptId, dept.parentId)
        )
        dept.parentName = parentDept?.deptName
        return dept
    }

    /**
     * 通过部门ID查询部门名称
     *
     * @param deptIds 部门ID串逗号分隔
     * @return 部门名称串逗号分隔
     */
    override fun selectDeptNameByIds(deptIds: String): String {
        val list: MutableList<String?> = ArrayList()
        for (id in StringUtilsExt.splitTo(deptIds, Convert::toLong)) {
            val vo = SpringUtilExt.getAopProxy(this).selectDeptById(id) ?: continue
            list.add(vo.deptName)
        }

        return list.joinToString(StringUtilsExt.SEPARATOR)
    }

    /**
     * 根据ID查询所有子部门数（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    override fun selectNormalChildrenDeptById(deptId: Long?): Long {
        return baseMapper.selectCount(
            MybatisKt.ktQuery<SysDeptEntity>()
                .eq(SysDeptEntity::status, UserConstants.DEPT_NORMAL)
                .apply(DataBaseHelper.findInSet(deptId, "ancestors"))
        )
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    override fun hasChildByDeptId(deptId: Long?): Boolean {
        return baseMapper.exists(
            MybatisKt.ktQuery<SysDeptEntity>()
                .eq(SysDeptEntity::parentId, deptId)
        )
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    override fun checkDeptExistUser(deptId: Long?): Boolean {
        return userMapper.exists(
            MybatisKt.ktQuery<SysUserEntity>()
                .eq(SysUserEntity::deptId, deptId)
        )
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    override fun checkDeptNameUnique(dept: SysDeptBo): Boolean {
        val exist = baseMapper.exists(
            MybatisKt.ktQuery<SysDeptEntity>()
                .eq(SysDeptEntity::deptName, dept.deptName)
                .eq(SysDeptEntity::parentId, dept.parentId)
                .ne(ObjectUtil.isNotNull(dept.deptId), SysDeptEntity::deptId, dept.deptId)
        )
        return !exist
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    override fun checkDeptDataScope(deptId: Long?) {
        if (ObjectUtil.isNull(deptId)) {
            return
        }
        if (LoginHelper.isSuperAdmin()) {
            return
        }
        val dept = baseMapper.selectDeptById(deptId)
        if (ObjectUtil.isNull(dept)) {
            throw ServiceException("没有权限访问部门数据！")
        }
    }

    /**
     * 新增保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
    override fun insertDept(bo: SysDeptBo): Int {
        val info = baseMapper.selectById(bo.parentId)
        // 如果父节点不为正常状态,则不允许新增子节点
        if (UserConstants.DEPT_NORMAL != info.status) {
            throw ServiceException("部门停用，不允许新增")
        }
        val dept = MapstructUtils.convert(bo, SysDeptEntity::class.java)
        dept.ancestors = info.ancestors + StringUtilsExt.SEPARATOR + dept.parentId
        return baseMapper.insert(dept)
    }

    /**
     * 修改保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
    @CacheEvict(cacheNames = [CacheNames.SYS_DEPT], key = "#bo.deptId")
    override fun updateDept(bo: SysDeptBo): Int {
        val dept: SysDeptEntity = MapstructUtils.convert(bo, SysDeptEntity::class.java)
        val oldDept = baseMapper.selectById(dept.deptId)
        if (oldDept.parentId != dept.parentId) {
            // 如果是新父部门 则校验是否具有新父部门权限 避免越权
            this.checkDeptDataScope(dept.parentId)
            val newParentDept = baseMapper.selectById(dept.parentId)
            if (ObjectUtil.isNotNull(newParentDept) && ObjectUtil.isNotNull(oldDept)) {
                val newAncestors = newParentDept.ancestors + StringUtilsExt.SEPARATOR + newParentDept.deptId
                val oldAncestors = oldDept.ancestors
                dept.ancestors = newAncestors
                updateDeptChildren(dept.deptId, newAncestors, oldAncestors)
            }
        }
        val result = baseMapper.updateById(dept)
        if (UserConstants.DEPT_NORMAL == dept.status && StringUtils.isNotEmpty(dept.ancestors)
            && !StringUtils.equals(UserConstants.DEPT_NORMAL, dept.ancestors)
        ) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept)
        }
        return result
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private fun updateParentDeptStatusNormal(dept: SysDeptEntity) {
        val ancestors = dept.ancestors
        val deptIds = Convert.toLongArray(ancestors)
        baseMapper.update(
            null, MybatisKt.ktUpdate<SysDeptEntity>()
                .set(SysDeptEntity::status, UserConstants.DEPT_NORMAL)
                .`in`(SysDeptEntity::deptId, Arrays.asList(*deptIds))
        )
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    private fun updateDeptChildren(deptId: Long?, newAncestors: String, oldAncestors: String?) {
        val children = baseMapper.selectList(
            MybatisKt.ktQuery<SysDeptEntity>()
                .apply(DataBaseHelper.findInSet(deptId, "ancestors"))
        )
        val list = ArrayList<SysDeptEntity>()
        for (child in children) {
            val dept = SysDeptEntity()
            dept.deptId = child.deptId
            dept.ancestors = child.ancestors.replaceFirst(oldAncestors ?: "", newAncestors)
            list.add(dept)
        }
        if (CollUtil.isNotEmpty(list)) {
            if (baseMapper.updateBatchById(list)) {
                list.forEach {
                    it.deptId?.let { it1 -> CacheUtils.evict(CacheNames.SYS_DEPT, it1) }
                }
            }
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @CacheEvict(cacheNames = [CacheNames.SYS_DEPT], key = "#deptId")
    override fun deleteDeptById(deptId: Long?): Int {
        return baseMapper.deleteById(deptId)
    }
}
