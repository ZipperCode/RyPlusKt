package com.zipper.modules.system.service.post

import cn.hutool.core.util.ObjectUtil
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysPostBo
import com.zipper.modules.system.domain.entity.SysPostEntity
import com.zipper.modules.system.domain.entity.SysUserPostEntity
import com.zipper.modules.system.domain.vo.SysPostVo
import com.zipper.modules.system.mapper.SysPostMapper
import com.zipper.modules.system.mapper.SysUserPostMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.MapstructUtils.convert

/**
 * 岗位信息 服务层处理
 *
 * @author Lion Li
 */
@Service
class SysPostServiceImpl(
    private val baseMapper: SysPostMapper,
    private val userPostMapper: SysUserPostMapper
) : ISysPostService {
    override fun selectPagePostList(post: SysPostBo, pageQuery: PageQuery): TableDataInfo<SysPostVo> {
        val lqw = buildQueryWrapper(post)
        val page = baseMapper.selectVoPage<Page<SysPostVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(page)
    }

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    override fun selectPostList(post: SysPostBo): List<SysPostVo> {
        val lqw = buildQueryWrapper(post)
        return baseMapper.selectVoList(lqw)
    }

    private fun buildQueryWrapper(bo: SysPostBo): KtQueryWrapper<SysPostEntity> {
        val lqw = KtQueryWrapper(SysPostEntity::class.java)
        lqw.like(StringUtils.isNotBlank(bo.postCode), SysPostEntity::postCode, bo.postCode)
        lqw.like(StringUtils.isNotBlank(bo.postName), SysPostEntity::postName, bo.postName)
        lqw.eq(StringUtils.isNotBlank(bo.status), SysPostEntity::status, bo.status)
        lqw.orderByAsc(SysPostEntity::postSort)
        return lqw
    }

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    override fun selectPostAll(): List<SysPostVo> {
        return baseMapper.selectVoList(QueryWrapper())
    }

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    override fun selectPostById(postId: Long?): SysPostVo? {
        return baseMapper.selectVoById(postId)
    }

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    override fun selectPostListByUserId(userId: Long?): List<Long> {
        return baseMapper.selectPostListByUserId(userId)
    }

    /**
     * 校验岗位名称是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    override fun checkPostNameUnique(post: SysPostBo): Boolean {
        val exist = baseMapper.exists(
            KtQueryWrapper(SysPostEntity::class.java)
                .eq(SysPostEntity::postName, post.postName)
                .ne(ObjectUtil.isNotNull(post.postId), SysPostEntity::postId, post.postId)
        )
        return !exist
    }

    /**
     * 校验岗位编码是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    override fun checkPostCodeUnique(post: SysPostBo): Boolean {
        val exist = baseMapper.exists(
            KtQueryWrapper(SysPostEntity::class.java)
                .eq(SysPostEntity::postCode, post.postCode)
                .ne(ObjectUtil.isNotNull(post.postId), SysPostEntity::postId, post.postId)
        )
        return !exist
    }

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    override fun countUserPostById(postId: Long?): Long {
        return userPostMapper.selectCount(KtQueryWrapper(SysUserPostEntity::class.java).eq(SysUserPostEntity::postId, postId))
    }

    /**
     * 删除岗位信息
     *
     * @param postId 岗位ID
     * @return 结果
     */
    override fun deletePostById(postId: Long?): Int {
        return baseMapper.deleteById(postId)
    }

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    override fun deletePostByIds(postIds: Array<Long>): Int {
        for (postId in postIds) {
            val post = baseMapper.selectById(postId)
            if (countUserPostById(postId) > 0) {
                throw ServiceException(String.format("%1\$s已分配，不能删除!", post.postName))
            }
        }
        return baseMapper.deleteBatchIds(listOf(*postIds))
    }

    /**
     * 新增保存岗位信息
     *
     * @param bo 岗位信息
     * @return 结果
     */
    override fun insertPost(bo: SysPostBo): Int {
        val post = convert(bo, SysPostEntity::class.java)
        return baseMapper.insert(post)
    }

    /**
     * 修改保存岗位信息
     *
     * @param bo 岗位信息
     * @return 结果
     */
    override fun updatePost(bo: SysPostBo): Int {
        val post = convert(bo, SysPostEntity::class.java)
        return baseMapper.updateById(post)
    }
}
