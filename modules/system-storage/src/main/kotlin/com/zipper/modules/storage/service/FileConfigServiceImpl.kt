package com.zipper.modules.storage.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.MapstructUtils
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.storage.client.FileClient
import com.zipper.modules.storage.client.FileClientFactory
import com.zipper.modules.storage.client.FileClientMasterDelegate
import com.zipper.modules.storage.domain.entity.SysFileConfigEntity
import com.zipper.modules.storage.domain.param.FileConfigPageParam
import com.zipper.modules.storage.domain.param.FileConfigSaveParam
import com.zipper.modules.storage.domain.vo.FileConfigVo
import com.zipper.modules.storage.enums.FileMessageConst
import com.zipper.modules.storage.mapper.FileConfigMapper
import org.springframework.stereotype.Service

/**
 * 文件配置实现类
 *
 */
@Service
class FileConfigServiceImpl(
    private val fileClientFactory: FileClientFactory,
    private val fileConfigMapper: FileConfigMapper
) : FileConfigService {

    private val masterFileClient: FileClientMasterDelegate = FileClientMasterDelegate(::getOrCreateFileClient)

    override fun createConfig(fileConfigSaveParam: FileConfigSaveParam): Int {
        val entity = fileConfigSaveParam.toEntity()
        return fileConfigMapper.insert(entity)
    }

    override fun updateConfig(fileConfigSaveParam: FileConfigSaveParam): Int {
        val entity = getFileConfig(fileConfigSaveParam.id)
        val configObject = fileConfigSaveParam.requireConfigObj()
        entity.config = configObject
        val row = fileConfigMapper.updateById(entity)
        fileClientFactory.updateFileClientIfExists(entity.requireConfigId, entity.requireStorage, entity.config)
        masterFileClient.checkMasterRelease(entity.requireConfigId)
        return row
    }

    override fun deleteConfig(ids: Array<Long>): Int {
        if (ids.isEmpty()) {
            return 0
        }
        val configList = fileConfigMapper.selectBatchIds(ids.toList())
        if (configList.isEmpty()) {
            throw ServiceException("数据空异常")
        }
        val allIds = mutableListOf<Long>()
        for (fileConfigEntity in configList) {
            if (fileConfigEntity.master) {
                throw FileMessageConst.UnableDeleteMasterConfigError
            }
            allIds.add(fileConfigEntity.requireConfigId)
        }

        return fileConfigMapper.deleteBatchIds(allIds)
    }

    override fun getConfig(id: Long): FileConfigVo? {
        return MapstructUtils.convertOrNull(fileConfigMapper.selectById(id), FileConfigVo::class.java)
    }

    override fun pageList(param: FileConfigPageParam): TableDataInfo<FileConfigVo> {
        val query = MybatisKt.ktQuery<SysFileConfigEntity>()
        val pageResult = fileConfigMapper.selectVoPage(param.build(), query, FileConfigVo::class.java)
        return TableDataInfo.build(pageResult)
    }

    override fun updateMaster(id: Long) {
        val entity = getFileConfig(id)
        fileConfigMapper.update(MybatisKt.ktUpdate<SysFileConfigEntity>().set(SysFileConfigEntity::master, false))
        entity.master = true
        fileConfigMapper.updateById(entity)
        masterFileClient.setValue(entity.requireConfigId)
    }

    override fun getFileClient(clientId: Long): FileClient {
        var client = fileClientFactory.getFileClient(clientId)
        if (client == null) {
            val entity = fileConfigMapper.selectById(clientId) ?: throw FileMessageConst.ClientNotFoundError
            client = fileClientFactory.createFileClient(entity.requireConfigId, entity.requireStorage, entity.config)
        }
        return client
    }

    override fun getMasterFileClient(): FileClient {
        return masterFileClient.getValue()
    }

    private fun getOrCreateFileClient(configId: Long): FileClient {
        val configEntity = if (configId == Long.MIN_VALUE) {
            requireMasterFileConfig()
        } else {
            getFileConfig(configId)
        }
        return fileClientFactory.createFileClient(configEntity.requireConfigId, configEntity.requireStorage, configEntity.config)
    }

    private fun getFileConfig(id: Long?): SysFileConfigEntity {
        return fileConfigMapper.selectById(id) ?: throw FileMessageConst.ConfigNotFoundError
    }

    private fun requireMasterFileConfig(): SysFileConfigEntity {
        return fileConfigMapper.selectOne(MybatisKt.ktQuery<SysFileConfigEntity>().eq(SysFileConfigEntity::master, true))
            ?: throw FileMessageConst.MasterConfigNotFoundError
    }
}