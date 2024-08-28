package com.zipper.modules.auth.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import org.springframework.stereotype.Service
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.modules.auth.domain.entity.SysSocialEntity
import com.zipper.modules.auth.domain.param.SysSocialSaveParam
import com.zipper.modules.auth.domain.vo.SysSocialVo
import com.zipper.modules.auth.mapper.SysSocialMapper

/**
 * 社会化关系Service业务层处理
 *
 * @author thiszhc
 * @date 2023-06-12
 */
@Service
class SysSocialServiceImpl(
    private val baseMapper: SysSocialMapper
) : ISysSocialService {
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
            KtQueryWrapper(SysSocialEntity::class.java)
                .eq(SysSocialEntity::userId, userId)
        )
    }


    /**
     * 新增社会化关系
     */
    override fun insert(param: SysSocialSaveParam): Boolean {
        val add = convert(param, SysSocialEntity::class.java)
        val flag = baseMapper.insert(add) > 0
        if (flag) {
            param.id = add.id
        }
        return flag
    }

    /**
     * 更新社会化关系
     */
    override fun update(param: SysSocialSaveParam): Boolean {
        val update = convert(param, SysSocialEntity::class.java)
        return baseMapper.updateById(update) > 0
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
