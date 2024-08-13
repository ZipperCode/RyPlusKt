package com.zipper.modules.system.service.client

import cn.hutool.crypto.SecureUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysClientBo
import com.zipper.modules.system.domain.entity.SysClientEntity
import com.zipper.modules.system.domain.vo.SysClientVo
import com.zipper.modules.system.mapper.SysClientMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import com.zipper.framework.core.utils.MapstructUtils
import java.util.function.Consumer

/**
 * 客户端管理Service业务层处理
 *
 * @author Michelle.Chung
 * @date 2023-06-18
 */
@Service
class SysClientServiceImpl(
    private val baseMapper: SysClientMapper
) : ISysClientService {


    /**
     * 查询客户端管理
     */
    override fun queryById(id: Long?): SysClientVo? {
        val vo = baseMapper.selectVoById(id) ?: return null
        vo.grantTypeList = vo.grantType.split(",")
        return vo
    }


    /**
     * 查询客户端管理
     */
    override fun queryByClientId(clientId: String?): SysClientEntity? {
        return baseMapper.selectOne(MybatisKt.ktQuery<SysClientEntity>().eq(SysClientEntity::clientId, clientId))
    }

    /**
     * 查询客户端管理列表
     */
    override fun queryPageList(bo: SysClientBo, pageQuery: PageQuery): TableDataInfo<SysClientVo> {
        val lqw = buildQueryWrapper(bo)
        val result = baseMapper.selectVoPage<Page<SysClientVo>>(pageQuery.build(), lqw)
        result.records.forEach(Consumer { r: SysClientVo ->
            r.grantTypeList = listOf(*r.grantType.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
        })
        return TableDataInfo.build(result)
    }

    /**
     * 查询客户端管理列表
     */
    override fun queryList(bo: SysClientBo): List<SysClientVo> {
        val lqw = buildQueryWrapper(bo)
        return baseMapper.selectVoList(lqw)
    }

    private fun buildQueryWrapper(bo: SysClientBo): KtQueryWrapper<SysClientEntity> {
        val lqw = MybatisKt.ktQuery<SysClientEntity>()
        lqw.eq(StringUtils.isNotBlank(bo.clientId), SysClientEntity::clientId, bo.clientId)
        lqw.eq(StringUtils.isNotBlank(bo.clientKey), SysClientEntity::clientKey, bo.clientKey)
        lqw.eq(StringUtils.isNotBlank(bo.clientSecret), SysClientEntity::clientSecret, bo.clientSecret)
        lqw.eq(StringUtils.isNotBlank(bo.status), SysClientEntity::status, bo.status)
        lqw.orderByAsc(SysClientEntity::id)
        return lqw
    }

    /**
     * 新增客户端管理
     */
    override fun insertByBo(bo: SysClientBo): Boolean {
        val add = MapstructUtils.convert(bo, SysClientEntity::class.java)
        validEntityBeforeSave(add)
        add.grantType = java.lang.String.join(",", bo.grantTypeList)
        // 生成clientid
        val clientKey = bo.clientKey
        val clientSecret = bo.clientSecret
        add.clientId = SecureUtil.md5(clientKey + clientSecret)
        val flag = baseMapper.insert(add) > 0
        if (flag) {
            bo.id = add.id
        }
        return flag
    }

    /**
     * 修改客户端管理
     */
    override fun updateByBo(bo: SysClientBo): Boolean {
        val update: SysClientEntity = MapstructUtils.convert(bo, SysClientEntity::class.java)
        validEntityBeforeSave(update)
        update.grantType = java.lang.String.join(",", bo.grantTypeList)
        return baseMapper.updateById(update) > 0
    }

    /**
     * 修改状态
     */
    override fun updateUserStatus(id: Long, status: String): Int {
        return baseMapper.update(
            null,
            MybatisKt.ktUpdate<SysClientEntity>()
                .set(SysClientEntity::status, status)
                .eq(SysClientEntity::id, id)
        )
    }

    /**
     * 保存前的数据校验
     */
    private fun validEntityBeforeSave(entity: SysClientEntity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除客户端管理
     */
    override fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0
    }
}
