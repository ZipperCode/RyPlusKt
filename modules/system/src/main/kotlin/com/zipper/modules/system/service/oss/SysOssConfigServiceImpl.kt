package com.zipper.modules.system.service.oss

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ObjectUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.json.utils.JsonUtils
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.oss.constant.OssConstant
import com.zipper.framework.redis.utils.CacheUtils.evict
import com.zipper.framework.redis.utils.CacheUtils.put
import com.zipper.framework.redis.utils.RedisUtils
import com.zipper.modules.system.domain.bo.SysOssConfigBo
import com.zipper.modules.system.domain.entity.SysOssConfigEntity
import com.zipper.modules.system.domain.vo.SysOssConfigVo
import com.zipper.modules.system.mapper.SysOssConfigMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.zipper.framework.core.constant.CacheNames
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.MapstructUtils.convert
import java.util.function.Consumer

/**
 * 对象存储配置Service业务层处理
 *
 * @author Lion Li
 * @author 孤舟烟雨
 * @date 2021-08-13
 */
@Service
class SysOssConfigServiceImpl(
    private val baseMapper: SysOssConfigMapper
) : ISysOssConfigService {
    /**
     * 项目启动时，初始化参数到缓存，加载配置类
     */
    override fun init() {
        val list = baseMapper.selectList()
        // 加载OSS初始化配置
        for (config in list) {
            val configKey = config.configKey ?: continue
            if ("0" == config.status) {
                RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, configKey)
            }
            put(CacheNames.SYS_OSS_CONFIG, config.configKey!!, JsonUtils.toJsonString(config))
        }
    }

    override fun queryById(ossConfigId: Long?): SysOssConfigVo? {
        return baseMapper.selectVoById(ossConfigId)
    }

    override fun queryPageList(bo: SysOssConfigBo, pageQuery: PageQuery): TableDataInfo<SysOssConfigVo> {
        val lqw = buildQueryWrapper(bo)
        val result = baseMapper.selectVoPage<Page<SysOssConfigVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(result)
    }


    private fun buildQueryWrapper(bo: SysOssConfigBo): KtQueryWrapper<SysOssConfigEntity> {
        val lqw = KtQueryWrapper(SysOssConfigEntity::class.java)
        lqw.eq(StringUtils.isNotBlank(bo.configKey), SysOssConfigEntity::configKey, bo.configKey)
        lqw.like(StringUtils.isNotBlank(bo.bucketName), SysOssConfigEntity::bucketName, bo.bucketName)
        lqw.eq(StringUtils.isNotBlank(bo.status), SysOssConfigEntity::status, bo.status)
        lqw.orderByAsc(SysOssConfigEntity::ossConfigId)
        return lqw
    }

    override fun insertByBo(bo: SysOssConfigBo): Boolean {
        var config = convert(bo, SysOssConfigEntity::class.java)
        validEntityBeforeSave(config)
        val flag = baseMapper.insert(config) > 0
        if (flag) {
            // 从数据库查询完整的数据做缓存
            config = baseMapper.selectById(config.ossConfigId)
            put(CacheNames.SYS_OSS_CONFIG, config.configKey!!, JsonUtils.toJsonString(config))
        }
        return flag
    }

    override fun updateByBo(bo: SysOssConfigBo): Boolean {
        var config = convert(bo, SysOssConfigEntity::class.java)
        validEntityBeforeSave(config)
        val luw = KtUpdateWrapper(SysOssConfigEntity::class.java)
        luw[ObjectUtil.isNull(config.prefix), SysOssConfigEntity::prefix] = ""
        luw[ObjectUtil.isNull(config.region), SysOssConfigEntity::region] = ""
        luw[ObjectUtil.isNull(config.ext1), SysOssConfigEntity::ext1] = ""
        luw[ObjectUtil.isNull(config.remark), SysOssConfigEntity::remark] = ""
        luw.eq(SysOssConfigEntity::ossConfigId, config.ossConfigId)
        val flag = baseMapper.update(config, luw) > 0
        if (flag) {
            // 从数据库查询完整的数据做缓存
            config = baseMapper.selectById(config.ossConfigId)
            put(CacheNames.SYS_OSS_CONFIG, config.configKey!!, JsonUtils.toJsonString(config))
        }
        return flag
    }

    /**
     * 保存前的数据校验
     */
    private fun validEntityBeforeSave(entity: SysOssConfigEntity) {
        if (StringUtils.isNotEmpty(entity.configKey)
            && !checkConfigKeyUnique(entity)
        ) {
            throw ServiceException("操作配置'" + entity.configKey + "'失败, 配置key已存在!")
        }
    }

    override fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean {
        if (isValid) {
            if (CollUtil.containsAny(ids, OssConstant.SYSTEM_DATA_IDS)) {
                throw ServiceException("系统内置, 不可删除!")
            }
        }
        val list: MutableList<SysOssConfigEntity> = CollUtil.newArrayList()
        for (configId in ids) {
            val config = baseMapper.selectById(configId)
            list.add(config)
        }
        val flag = baseMapper.deleteBatchIds(ids) > 0
        if (flag) {
            list.forEach(Consumer { sysOssConfigEntity: SysOssConfigEntity ->
                evict(
                    CacheNames.SYS_OSS_CONFIG,
                    sysOssConfigEntity.configKey!!
                )
            })
        }
        return flag
    }

    /**
     * 判断configKey是否唯一
     */
    private fun checkConfigKeyUnique(sysOssConfigEntity: SysOssConfigEntity): Boolean {
        val ossConfigId = if (ObjectUtil.isNull(sysOssConfigEntity.ossConfigId)) -1L else sysOssConfigEntity.ossConfigId!!

        val info = baseMapper.selectOne(
            KtQueryWrapper(sysOssConfigEntity)
                .select(SysOssConfigEntity::ossConfigId, SysOssConfigEntity::configKey)
                .eq(SysOssConfigEntity::configKey, sysOssConfigEntity.configKey)
        )
        if (ObjectUtil.isNotNull(info) && info.ossConfigId != ossConfigId) {
            return false
        }
        return true
    }

    /**
     * 启用禁用状态
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun updateOssConfigStatus(bo: SysOssConfigBo): Int {
        val sysOssConfigEntity = convert(bo, SysOssConfigEntity::class.java)

        var row =  baseMapper.update(
            KtUpdateWrapper(sysOssConfigEntity)
                .set(SysOssConfigEntity::status, "1")
        )
        row += baseMapper.updateById(sysOssConfigEntity)
        if (row > 0) {
            RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, sysOssConfigEntity.configKey)
        }
        return row
    }
}
