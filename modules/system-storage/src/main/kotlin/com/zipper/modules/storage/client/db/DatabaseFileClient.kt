package com.zipper.modules.storage.client.db

import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.modules.storage.client.BaseFileClient
import com.zipper.modules.storage.domain.entity.SysFileContentEntity
import com.zipper.modules.storage.mapper.FileContentMapper

/**
 * 数据库文件存储客户端
 */
class DatabaseFileClient(
    id: Long, config: DataBaseFileClientConfig
) : BaseFileClient<DataBaseFileClientConfig>(id, config) {

    private val fileContentMapper: FileContentMapper by lazy {
        SpringUtilExt.getBeanByType(FileContentMapper::class.java)
    }

    override fun upload(content: ByteArray, path: String, type: String): String {
        fileContentMapper.insert(
            SysFileContentEntity().apply {
                configId = getId()
                this.path = path
                this.content = content
            }
        )
        return getFormatUrl(path)
    }

    override fun delete(path: String) {
        fileContentMapper.delete(
            MybatisKt.ktQuery<SysFileContentEntity>()
                .eq(SysFileContentEntity::configId, getId())
                .eq(SysFileContentEntity::path, path)
        )
    }

    override fun getContent(path: String): ByteArray {
        val list = fileContentMapper.selectList(
            MybatisKt.ktQuery<SysFileContentEntity>()
                .eq(SysFileContentEntity::configId, getId())
                .eq(SysFileContentEntity::path, path)
                .orderByDesc(SysFileContentEntity::id)
        )
        val content = list.firstOrNull()?.content ?: throw ServiceException("文件内容不存在")
        return content
    }


}