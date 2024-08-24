package com.zipper.modules.storage.client.local

import cn.hutool.core.io.FileUtil
import com.zipper.modules.storage.client.BaseFileClient
import java.io.File

class LocalFileClient(
    id: Long, config: LocalFileClientConfig
) : BaseFileClient<LocalFileClientConfig>(id, config) {
    override fun upload(content: ByteArray, path: String, type: String): String {
        val filePath = File(getConfig().basePath, path)
        filePath.parentFile?.takeIf { !it.exists() }?.run {
            mkdirs()
        }
        FileUtil.writeBytes(content, filePath)
        return getFormatUrl(path)
    }

    override fun delete(path: String) {
        val filePath = File(getConfig().basePath, path)
        FileUtil.del(filePath)
    }

    override fun getContent(path: String): ByteArray {
        val filePath = File(getConfig().basePath, path)
        return FileUtil.readBytes(filePath)
    }
}