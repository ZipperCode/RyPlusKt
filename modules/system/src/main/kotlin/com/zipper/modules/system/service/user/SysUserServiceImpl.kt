package com.zipper.modules.system.service.user

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.ObjectUtil
import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.mybatis.helper.DataBaseHelper
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.modules.system.domain.bo.SysUserBo
import com.zipper.modules.system.domain.entity.SysDeptEntity
import com.zipper.modules.system.domain.entity.SysUserEntity
import com.zipper.modules.system.domain.entity.SysUserPostEntity
import com.zipper.modules.system.domain.entity.SysUserRoleEntity
import com.zipper.modules.system.domain.vo.SysPostVo
import com.zipper.modules.system.domain.vo.SysUserVo
import com.zipper.modules.system.mapper.*
import lombok.RequiredArgsConstructor
import org.apache.commons.lang3.StringUtils
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.zipper.framework.core.constant.CacheNames
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.modules.ITenantApi
import com.zipper.framework.core.service.UserService
import com.zipper.framework.core.utils.MapstructUtils
import com.zipper.framework.core.utils.StreamUtils
import com.zipper.framework.core.utils.StringUtilsExt
import com.zipper.framework.tanent.helper.TenantHelper

/**
 * 用户 业务层处理
 *
 * @author Lion Li
 */

@Service
class SysUserServiceImpl(
    private val baseMapper: SysUserMapper,
    private val deptMapper: SysDeptMapper,
    private val roleMapper: SysRoleMapper,
    private val postMapper: SysPostMapper,
    private val userRoleMapper: SysUserRoleMapper,
    private val userPostMapper: SysUserPostMapper
) : ISysUserService, UserService {


    override fun selectPageUserList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        val page = baseMapper.selectPageUserList(pageQuery.build(), this.buildQueryWrapper(user))
        return TableDataInfo.build(page)
    }

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    override fun selectUserList(user: SysUserBo): List<SysUserVo> {
        return baseMapper.selectUserList(this.buildQueryWrapper(user))
    }

    private fun buildQueryWrapper(user: SysUserBo): Wrapper<SysUserEntity> {
        val params = user.params
        val wrapper = Wrappers.query<SysUserEntity>()
        wrapper.eq("u.del_flag", UserConstants.USER_NORMAL)
            .eq(ObjectUtil.isNotNull(user.userId), "u.user_id", user.userId)
            .like(StringUtils.isNotBlank(user.userName), "u.user_name", user.userName)
            .eq(StringUtils.isNotBlank(user.status), "u.status", user.status)
            .like(StringUtils.isNotBlank(user.phonenumber), "u.phonenumber", user.phonenumber)
            .between(
                params["beginTime"] != null && params["endTime"] != null,
                "u.create_time", params["beginTime"], params["endTime"]
            )
            .and(ObjectUtil.isNotNull(user.deptId)) { w: QueryWrapper<SysUserEntity> ->
                val deptList = deptMapper.selectList(
                    MybatisKt.ktQuery<SysDeptEntity>()
                        .select(SysDeptEntity::deptId)
                        .apply(DataBaseHelper.findInSet(user.deptId, "ancestors"))
                )

                val ids = deptList.mapNotNull { it.deptId }.toMutableList()
                user.deptId?.let { ids.add(it) }
                w.`in`("u.dept_id", ids)
            }.orderByAsc("u.user_id")
        return wrapper
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    override fun selectAllocatedList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        val wrapper = Wrappers.query<SysUserEntity>()
        wrapper.eq("u.del_flag", UserConstants.USER_NORMAL)
            .eq(ObjectUtil.isNotNull(user.roleId), "r.role_id", user.roleId)
            .like(StringUtils.isNotBlank(user.userName), "u.user_name", user.userName)
            .eq(StringUtils.isNotBlank(user.status), "u.status", user.status)
            .like(StringUtils.isNotBlank(user.phonenumber), "u.phonenumber", user.phonenumber)
            .orderByAsc("u.user_id")
        val page = baseMapper.selectAllocatedList(pageQuery.build(), wrapper)
        return TableDataInfo.build(page)
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    override fun selectUnallocatedList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        val userIds = userRoleMapper.selectUserIdsByRoleId(user.roleId)
        val wrapper = Wrappers.query<SysUserEntity>()
        wrapper.eq("u.del_flag", UserConstants.USER_NORMAL)
            .and { w: QueryWrapper<SysUserEntity?> -> w.ne("r.role_id", user.roleId).or().isNull("r.role_id") }
            .notIn(CollUtil.isNotEmpty(userIds), "u.user_id", userIds)
            .like(StringUtils.isNotBlank(user.userName), "u.user_name", user.userName)
            .like(StringUtils.isNotBlank(user.phonenumber), "u.phonenumber", user.phonenumber)
            .orderByAsc("u.user_id")
        val page = baseMapper.selectUnallocatedList(pageQuery.build(), wrapper)
        return TableDataInfo.build(page)
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    override fun selectUserByUserName(userName: String?): SysUserVo? {
        return baseMapper.selectUserByUserName(userName)
    }

    /**
     * 通过手机号查询用户
     *
     * @param phonenumber 手机号
     * @return 用户对象信息
     */
    override fun selectUserByPhonenumber(phonenumber: String?): SysUserVo? {
        return baseMapper.selectUserByPhonenumber(phonenumber)
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    override fun selectUserById(userId: Long?): SysUserVo? {
        return baseMapper.selectUserById(userId)
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    override fun selectUserRoleGroup(userName: String?): String? {
        val list = roleMapper.selectRolesByUserName(userName)
        if (list.isEmpty()) {
            return StringUtils.EMPTY
        }
        return list.mapNotNull { it.roleId }.joinToString(StringUtilsExt.SEPARATOR)
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    override fun selectUserPostGroup(userName: String?): String? {
        val list = postMapper.selectPostsByUserName(userName)
        if (CollUtil.isEmpty(list)) {
            return StringUtils.EMPTY
        }
        return StreamUtils.join(list, SysPostVo::postName)
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    override fun checkUserNameUnique(user: SysUserBo): Boolean {
        val exist = baseMapper.exists(
            MybatisKt.ktQuery<SysUserEntity>()
                .eq(SysUserEntity::userName, user.userName)
                .ne(ObjectUtil.isNotNull(user.userId), SysUserEntity::userId, user.userId)
        )
        return !exist
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     */
    override fun checkPhoneUnique(user: SysUserBo): Boolean {
        val exist = baseMapper.exists(
            MybatisKt.ktQuery<SysUserEntity>()
                .eq(SysUserEntity::phonenumber, user.phonenumber)
                .ne(ObjectUtil.isNotNull(user.userId), SysUserEntity::userId, user.userId)
        )
        return !exist
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     */
    override fun checkEmailUnique(user: SysUserBo): Boolean {
        val exist = baseMapper.exists(
            MybatisKt.ktQuery<SysUserEntity>()
                .eq(SysUserEntity::email, user.email)
                .ne(ObjectUtil.isNotNull(user.userId), SysUserEntity::userId, user.userId)
        )
        return !exist
    }

    /**
     * 校验用户是否允许操作
     *
     * @param userId 用户ID
     */
    override fun checkUserAllowed(userId: Long?) {
        if (ObjectUtil.isNotNull(userId) && LoginHelper.isSuperAdmin(userId)) {
            throw ServiceException("不允许操作超级管理员用户")
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    override fun checkUserDataScope(userId: Long?) {
        if (ObjectUtil.isNull(userId)) {
            return
        }
        if (LoginHelper.isSuperAdmin()) {
            return
        }
        if (ObjectUtil.isNull(baseMapper.selectUserById(userId))) {
            throw ServiceException("没有权限访问用户数据！")
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insertUser(user: SysUserBo): Int {
        val sysUserEntity = MapstructUtils.convert(user, SysUserEntity::class.java)
        // 新增用户信息
        val rows = baseMapper.insert(sysUserEntity)
        user.userId = sysUserEntity.userId
        // 新增用户岗位关联
        insertUserPost(user, false)
        // 新增用户与角色管理
        insertUserRole(user, false)
        return rows
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    override fun registerUser(user: SysUserBo, tenantId: String?): Boolean {
        user.createBy = user.userId
        user.updateBy = user.userId
        val sysUserEntity = MapstructUtils.convert(user, SysUserEntity::class.java)
        sysUserEntity.tenantId = tenantId
        return baseMapper.insert(sysUserEntity) > 0
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun updateUser(user: SysUserBo): Int {
        // 新增用户与角色管理
        insertUserRole(user, true)
        // 新增用户与岗位管理
        insertUserPost(user, true)
        val sysUserEntity = MapstructUtils.convert(user, SysUserEntity::class.java)
        // 防止错误更新后导致的数据误删除
        val flag = baseMapper.updateById(sysUserEntity)
        if (flag < 1) {
            throw ServiceException("修改用户" + user.userName + "信息失败")
        }
        return flag
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insertUserAuth(userId: Long?, roleIds: Array<Long>) {
        insertUserRole(userId, roleIds, true)
    }

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 帐号状态
     * @return 结果
     */
    override fun updateUserStatus(userId: Long?, status: String?): Int {
        return baseMapper.update(
            null,
            MybatisKt.ktUpdate<SysUserEntity>()
                .set(SysUserEntity::status, status)
                .eq(SysUserEntity::userId, userId)
        )
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    override fun updateUserProfile(user: SysUserBo): Int {
        return baseMapper.update(
            null,
            MybatisKt.ktUpdate<SysUserEntity>()
                .set(ObjectUtil.isNotNull(user.nickName), SysUserEntity::nickName, user.nickName)
                .set(SysUserEntity::phonenumber, user.phonenumber)
                .set(SysUserEntity::email, user.email)
                .set(SysUserEntity::sex, user.sex)
                .eq(SysUserEntity::userId, user.userId)
        )
    }

    /**
     * 修改用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    override fun updateUserAvatar(userId: Long?, avatar: Long?): Boolean {
        return baseMapper.update(
            null,
            MybatisKt.ktUpdate<SysUserEntity>()
                .set(SysUserEntity::avatar, avatar)
                .eq(SysUserEntity::userId, userId)
        ) > 0
    }

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @return 结果
     */
    override fun resetUserPwd(userId: Long?, password: String?): Int {
        return baseMapper.update(
            null,
            MybatisKt.ktUpdate<SysUserEntity>()
                .set(SysUserEntity::password, password)
                .eq(SysUserEntity::userId, userId)
        )
    }

    /**
     * 新增用户角色信息
     *
     * @param user  用户对象
     * @param clear 清除已存在的关联数据
     */
    private fun insertUserRole(user: SysUserBo, clear: Boolean) {
        this.insertUserRole(user.userId, user.roleIds, clear)
    }

    /**
     * 新增用户岗位信息
     *
     * @param user  用户对象
     * @param clear 清除已存在的关联数据
     */
    private fun insertUserPost(user: SysUserBo, clear: Boolean) {
        val posts = user.postIds
        if (ArrayUtil.isNotEmpty<Long>(posts)) {
            if (clear) {
                // 删除用户与岗位关联
                userPostMapper.delete(MybatisKt.ktQuery<SysUserPostEntity>().eq(SysUserPostEntity::userId, user.userId))
            }
            // 新增用户与岗位管理
            val list: List<SysUserPostEntity> = StreamUtils.toList(java.util.List.of(*posts)) { postId ->
                val up = SysUserPostEntity()
                up.userId = user.userId
                up.postId = postId
                up
            }
            userPostMapper.insertBatch(list)
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     * @param clear   清除已存在的关联数据
     */
    private fun insertUserRole(userId: Long?, roleIds: Array<Long>, clear: Boolean) {
        if (ArrayUtil.isNotEmpty<Long>(roleIds)) {
            // 判断是否具有此角色的操作权限
            val roles = roleMapper.selectRoleList(MybatisKt.ktQuery())
            if (CollUtil.isEmpty(roles)) {
                throw ServiceException("没有权限访问角色的数据")
            }

            val roleList = roles.mapNotNull { it.roleId }.toMutableList()
            if (!LoginHelper.isSuperAdmin(userId)) {
                roleList.remove(UserConstants.SUPER_ADMIN_ID)
            }
            val canDoRoleList: List<Long?> = StreamUtils.filter(java.util.List.of(*roleIds), roleList::contains)
            if (CollUtil.isEmpty(canDoRoleList)) {
                throw ServiceException("没有权限访问角色的数据")
            }
            if (clear) {
                // 删除用户与角色关联
                userRoleMapper.delete(MybatisKt.ktQuery<SysUserRoleEntity>().eq(SysUserRoleEntity::userId, userId))
            }
            // 新增用户与角色管理
            val list: List<SysUserRoleEntity> = StreamUtils.toList(canDoRoleList) { roleId ->
                val ur = SysUserRoleEntity()
                ur.userId = userId
                ur.roleId = roleId
                ur
            }
            userRoleMapper.insertBatch(list)
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun deleteUserById(userId: Long?): Int {
        // 删除用户与角色关联
        userRoleMapper.delete(MybatisKt.ktQuery<SysUserRoleEntity>().eq(SysUserRoleEntity::userId, userId))
        // 删除用户与岗位表
        userPostMapper.delete(MybatisKt.ktQuery<SysUserPostEntity>().eq(SysUserPostEntity::userId, userId))
        // 防止更新失败导致的数据删除
        val flag = baseMapper.deleteById(userId)
        if (flag < 1) {
            throw ServiceException("删除用户失败!")
        }
        return flag
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun deleteUserByIds(userIds: Array<Long>): Int {
        for (userId in userIds) {
            checkUserAllowed(userId)
            checkUserDataScope(userId)
        }
        val ids = java.util.List.of(*userIds)
        // 删除用户与角色关联
        userRoleMapper.delete(MybatisKt.ktQuery<SysUserRoleEntity>().`in`(SysUserRoleEntity::userId, ids))
        // 删除用户与岗位表
        userPostMapper.delete(MybatisKt.ktQuery<SysUserPostEntity>().`in`(SysUserPostEntity::userId, ids))
        // 防止更新失败导致的数据删除
        val flag = baseMapper.deleteBatchIds(ids)
        if (flag < 1) {
            throw ServiceException("删除用户失败!")
        }
        return flag
    }

    /**
     * 通过部门id查询当前部门所有用户
     *
     * @param deptId
     * @return
     */
    override fun selectUserListByDept(deptId: Long?): List<SysUserVo> {
        val lqw = MybatisKt.ktQuery<SysUserEntity>()
        lqw.eq(SysUserEntity::deptId, deptId)
        lqw.orderByAsc(SysUserEntity::userId)
        return baseMapper.selectVoList(lqw)
    }

    @Cacheable(cacheNames = [CacheNames.SYS_USER_NAME], key = "#userId")
    override fun selectUserNameById(userId: Long): String? {
        val sysUserEntity = baseMapper.selectOne(
            MybatisKt.ktQuery<SysUserEntity>()
                .select(SysUserEntity::userName).eq(SysUserEntity::userId, userId)
        )
        return if (ObjectUtil.isNull(sysUserEntity)) null else sysUserEntity.userName
    }

    @Cacheable(cacheNames = [CacheNames.SYS_NICKNAME], key = "#userId")
    override fun selectNicknameById(userId: Long): String? {
        val sysUserEntity = baseMapper.selectOne(
            MybatisKt.ktQuery<SysUserEntity>()
                .select(SysUserEntity::nickName).eq(SysUserEntity::userId, userId)
        )
        return if (ObjectUtil.isNull(sysUserEntity)) null else sysUserEntity.nickName
    }

    override fun checkAccountBalance(tenantId: String?): Boolean {
        if (tenantId.isNullOrEmpty()) {
            return true
        }
        return ITenantApi.getImpl()?.checkAccountBalance(tenantId) {
            baseMapper.selectCount(
                MybatisKt.ktQuery<SysUserEntity>()
                    .eq(SysUserEntity::tenantId, tenantId)
            )
        } ?: false
    }

}
