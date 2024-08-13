package com.zipper.modules.system.service.post

import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysPostBo
import com.zipper.modules.system.domain.vo.SysPostVo

/**
 * 岗位信息 服务层
 *
 * @author Lion Li
 */
interface ISysPostService {
    fun selectPagePostList(post: SysPostBo, pageQuery: PageQuery): TableDataInfo<SysPostVo>

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位列表
     */
    fun selectPostList(post: SysPostBo): List<SysPostVo>

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    fun selectPostAll(): List<SysPostVo>

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    fun selectPostById(postId: Long?): SysPostVo?

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    fun selectPostListByUserId(userId: Long?): List<Long>

    /**
     * 校验岗位名称
     *
     * @param post 岗位信息
     * @return 结果
     */
    fun checkPostNameUnique(post: SysPostBo): Boolean

    /**
     * 校验岗位编码
     *
     * @param post 岗位信息
     * @return 结果
     */
    fun checkPostCodeUnique(post: SysPostBo): Boolean

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    fun countUserPostById(postId: Long?): Long

    /**
     * 删除岗位信息
     *
     * @param postId 岗位ID
     * @return 结果
     */
    fun deletePostById(postId: Long?): Int

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    fun deletePostByIds(postIds: Array<Long>): Int

    /**
     * 新增保存岗位信息
     *
     * @param bo 岗位信息
     * @return 结果
     */
    fun insertPost(bo: SysPostBo): Int

    /**
     * 修改保存岗位信息
     *
     * @param bo 岗位信息
     * @return 结果
     */
    fun updatePost(bo: SysPostBo): Int
}
