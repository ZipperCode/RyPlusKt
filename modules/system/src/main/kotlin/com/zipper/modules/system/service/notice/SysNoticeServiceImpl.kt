package com.zipper.modules.system.service.notice

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysNoticeBo
import com.zipper.modules.system.domain.entity.SysNoticeEntity
import com.zipper.modules.system.domain.vo.SysNoticeVo
import com.zipper.modules.system.mapper.SysNoticeMapper
import com.zipper.modules.system.mapper.SysUserMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import com.zipper.framework.core.utils.MapstructUtils.convert

/**
 * 公告 服务层实现
 *
 * @author Lion Li
 */
@Service
class SysNoticeServiceImpl(
    private val baseMapper: SysNoticeMapper,
    private val userMapper: SysUserMapper
) : ISysNoticeService {
    override fun selectPageNoticeList(notice: SysNoticeBo, pageQuery: PageQuery): TableDataInfo<SysNoticeVo> {
        val lqw = buildQueryWrapper(notice)
        val page = baseMapper.selectVoPage<Page<SysNoticeVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(page)
    }

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    override fun selectNoticeById(noticeId: Long?): SysNoticeVo {
        return baseMapper.selectVoById(noticeId)!!
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    override fun selectNoticeList(notice: SysNoticeBo): List<SysNoticeVo> {
        val lqw = buildQueryWrapper(notice)
        return baseMapper.selectVoList(lqw)
    }

    private fun buildQueryWrapper(bo: SysNoticeBo): KtQueryWrapper<SysNoticeEntity> {
        val lqw = MybatisKt.ktQuery<SysNoticeEntity>()
        lqw.like(StringUtils.isNotBlank(bo.noticeTitle), SysNoticeEntity::noticeTitle, bo.noticeTitle)
        lqw.eq(StringUtils.isNotBlank(bo.noticeType), SysNoticeEntity::noticeType, bo.noticeType)
        if (StringUtils.isNotBlank(bo.createByName)) {
            val sysUser = userMapper.selectUserByUserName(bo.createByName)
            lqw.eq(SysNoticeEntity::createBy, sysUser?.userId)
        }
        lqw.orderByAsc(SysNoticeEntity::noticeId)
        return lqw
    }

    /**
     * 新增公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    override fun insertNotice(bo: SysNoticeBo): Int {
        val notice = convert(bo, SysNoticeEntity::class.java)
        return baseMapper.insert(notice)
    }

    /**
     * 修改公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    override fun updateNotice(bo: SysNoticeBo): Int {
        val notice = convert(bo, SysNoticeEntity::class.java)
        return baseMapper.updateById(notice)
    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    override fun deleteNoticeById(noticeId: Long?): Int {
        return baseMapper.deleteById(noticeId)
    }

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    override fun deleteNoticeByIds(noticeIds: Array<Long>): Int {
        return baseMapper.deleteBatchIds(listOf(*noticeIds))
    }
}
