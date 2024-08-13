package com.zipper.modules.system.service.config

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import com.baomidou.dynamic.datasource.annotation.DS
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.redis.utils.CacheUtils
import com.zipper.framework.tanent.helper.TenantHelper
import com.zipper.modules.system.domain.bo.SysConfigBo
import com.zipper.modules.system.domain.entity.SysConfigEntity
import com.zipper.modules.system.domain.vo.SysConfigVo
import com.zipper.modules.system.mapper.SysConfigMapper
import lombok.RequiredArgsConstructor
import org.apache.commons.lang3.StringUtils
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import com.zipper.framework.core.constant.CacheNames
import com.zipper.framework.core.constant.UserConstants
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.service.ConfigService
import com.zipper.framework.core.utils.MapstructUtils
import com.zipper.framework.core.utils.SpringUtilExt
import java.util.*
import java.util.function.Supplier

/**
 * 参数配置 服务层实现
 *
 * @author Lion Li
 */
@Service
class SysConfigServiceImpl(
    private val baseMapper: SysConfigMapper
) : ISysConfigService, ConfigService {


    override fun selectPageConfigList(config: SysConfigBo, pageQuery: PageQuery): TableDataInfo<SysConfigVo> {
        val lqw = buildQueryWrapper(config)
        val page = baseMapper.selectVoPage<Page<SysConfigVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(page)
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @DS("master")
    override fun selectConfigById(configId: Long?): SysConfigVo? {
        return baseMapper.selectVoById(configId)
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Cacheable(cacheNames = [CacheNames.SYS_CONFIG], key = "#configKey")
    override fun selectConfigByKey(configKey: String?): String {
        val retConfig = baseMapper.selectOne(
            MybatisKt.ktQuery<SysConfigEntity>()
                .eq(SysConfigEntity::configKey, configKey)
        )
        if (ObjectUtil.isNotNull(retConfig)) {
            return retConfig.configValue!!
        }
        return ""
    }

    /**
     * 获取注册开关
     * @param tenantId 租户id
     * @return true开启，false关闭
     */
    override fun selectRegisterEnabled(tenantId: String?): Boolean {
        val retConfig: SysConfigEntity = TenantHelper.dynamic(tenantId, Supplier {
            baseMapper.selectOne(
                MybatisKt.ktQuery<SysConfigEntity>()
                    .eq(SysConfigEntity::configKey, "sys.account.registerUser")
            )
        })
        if (ObjectUtil.isNull(retConfig)) {
            return false
        }
        return Convert.toBool(retConfig.configValue)
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    override fun selectConfigList(config: SysConfigBo): List<SysConfigVo> {
        val lqw = buildQueryWrapper(config)
        return baseMapper.selectVoList(lqw)
    }

    private fun buildQueryWrapper(bo: SysConfigBo): KtQueryWrapper<SysConfigEntity> {
        val params: Map<String, Any?> = bo.params
        val lqw = MybatisKt.ktQuery<SysConfigEntity>()
        lqw.like(StringUtils.isNotBlank(bo.configName), SysConfigEntity::configName, bo.configName)
        lqw.eq(StringUtils.isNotBlank(bo.configType), SysConfigEntity::configType, bo.configType)
        lqw.like(StringUtils.isNotBlank(bo.configKey), SysConfigEntity::configKey, bo.configKey)
        lqw.between(
            params["beginTime"] != null && params["endTime"] != null,
            SysConfigEntity::createTime, params["beginTime"], params["endTime"]
        )
        lqw.orderByAsc(SysConfigEntity::configId)
        return lqw
    }

    /**
     * 新增参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    @CachePut(cacheNames = [CacheNames.SYS_CONFIG], key = "#bo.configKey")
    override fun insertConfig(bo: SysConfigBo): String? {
        val config = MapstructUtils.convert(bo, SysConfigEntity::class.java)
        val row = baseMapper.insert(config)
        if (row > 0) {
            return config.configValue
        }
        throw ServiceException("操作失败")
    }

    /**
     * 修改参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    @CachePut(cacheNames = [CacheNames.SYS_CONFIG], key = "#bo.configKey")
    override fun updateConfig(bo: SysConfigBo): String? {
        var row = 0
        val config = MapstructUtils.convert(bo, SysConfigEntity::class.java)
        if (config.configId != null) {
            val temp = baseMapper.selectById(config.configId)
            if (!StringUtils.equals(temp.configKey, config.configKey)) {
                CacheUtils.evict(CacheNames.SYS_CONFIG, temp!!.configKey!!)
            }
            row = baseMapper.updateById(config)
        } else {
            row = baseMapper.update(
                config, MybatisKt.ktQuery<SysConfigEntity>()
                    .eq(SysConfigEntity::configKey, config.configKey)
            )
        }
        if (row > 0) {
            return config.configValue
        }
        throw ServiceException("操作失败")
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    override fun deleteConfigByIds(configIds: Array<Long>) {
        for (configId in configIds) {
            val config = baseMapper.selectById(configId)
            if (StringUtils.equals(UserConstants.YES, config.configType)) {
                throw ServiceException(String.format("内置参数【%1\$s】不能删除 ", config.configKey))
            }
            CacheUtils.evict(CacheNames.SYS_CONFIG, config.configKey!!)
        }
        baseMapper.deleteBatchIds(Arrays.asList(*configIds))
    }

    /**
     * 重置参数缓存数据
     */
    override fun resetConfigCache() {
        CacheUtils.clear(CacheNames.SYS_CONFIG)
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    override fun checkConfigKeyUnique(config: SysConfigBo): Boolean {
        val configId = if (ObjectUtil.isNull(config.configId)) -1L else config.configId
        val info = baseMapper.selectOne(MybatisKt.ktQuery<SysConfigEntity>().eq(SysConfigEntity::configKey, config.configKey))
        if (ObjectUtil.isNotNull(info) && info.configId != configId) {
            return false
        }
        return true
    }

    /**
     * 根据参数 key 获取参数值
     *
     * @param configKey 参数 key
     * @return 参数值
     */
    override fun getConfigValue(configKey: String): String {
        return SpringUtilExt.getAopProxy(this).selectConfigByKey(configKey)
    }
}
