package com.zipper.modules.system.service.social

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.zipper.modules.system.domain.bo.SysSocialBo
import com.zipper.modules.system.domain.entity.SysSocialEntity
import com.zipper.modules.system.domain.vo.SysSocialVo
import com.zipper.modules.system.mapper.SysSocialMapper
import org.springframework.stereotype.Service
import com.zipper.framework.core.utils.MapstructUtils.convert

/**
 * 社会化关系Service业务层处理
 *
 * @author thiszhc
 * @date 2023-06-12
 */
@Service
class SysSocialServiceImpl(private val baseMapper: SysSocialMapper) : ISysSocialService {
    /**
     * 查询社会化关系
     */
    override fun queryById(id: String?): SysSocialVo? {
        return baseMapper.selectVoById(id)
    }

    /**
     * 授权列表
     */
    override fun queryList(): List<SysSocialVo> {
        return baseMapper.selectVoList()
    }

    override fun queryListByUserId(userId: Long?): List<SysSocialVo> {
        return baseMapper.selectVoList(
            KtQueryWrapper(SysSocialEntity::class.java).eq(SysSocialEntity::userId, userId)
        )
    }


    /**
     * 新增社会化关系
     */
    override fun insertByBo(bo: SysSocialBo): Boolean {
        val add = convert(bo, SysSocialEntity::class.java)
        validEntityBeforeSave(add)
        val flag = baseMapper.insert(add) > 0
        if (flag) {
            bo.id = add.id
        }
        return flag
    }

    /**
     * 更新社会化关系
     */
    override fun updateByBo(bo: SysSocialBo): Boolean {
        val update = convert(bo, SysSocialEntity::class.java)
        validEntityBeforeSave(update)
        return baseMapper.updateById(update) > 0
    }

    /**
     * 保存前的数据校验
     */
    private fun validEntityBeforeSave(entity: SysSocialEntity) {
        //TODO 做一些数据校验,如唯一约束
    }


    /**
     * 删除社会化关系
     */
    override fun deleteWithValidById(id: Long?): Boolean {
        return baseMapper.deleteById(id) > 0
    }


    /**
     * 根据 authId 查询用户信息
     *
     * @param authId 认证id
     * @return 授权信息
     */
    override fun selectByAuthId(authId: String?): List<SysSocialVo> {
        return baseMapper.selectVoList(KtQueryWrapper(SysSocialEntity::class.java).eq(SysSocialEntity::authId, authId))
    }
}
