package com.zipper.modules.storage.client

import com.zipper.framework.core.ext.log
import com.zipper.modules.storage.enums.FileStorageEnum
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class FileClientFactoryImpl : FileClientFactory {
    /**
     * 文件客户端 Map
     * key：配置编号
     */
    private val clients: ConcurrentMap<Long, FileClient> = ConcurrentHashMap<Long, FileClient>()

    override fun getFileClient(configId: Long): FileClient? {
        val client = clients[configId]
        if (client == null) {
            log.error("[getFileClient][配置编号({}) 找不到客户端]", configId)
        }
        return client
    }

    override fun <Config : FileClientConfig> createFileClient(configId: Long, storage: Int, config: Config): FileClient {
        val client = create<Config>(configId, storage, config)
        clients[configId] = client
        return client
    }

    override fun <Config : FileClientConfig> updateFileClientIfExists(configId: Long, storage: Int, config: Config) {
        var client = clients[configId] ?: return
        if (client.getConfig() != config) {
            client = create(configId, storage, config)
            clients[configId] = client
        }
    }

    private fun <Config : FileClientConfig> create(configId: Long, storage: Int, config: Config): FileClient {
        val storageEnum = FileStorageEnum.getByStorage(storage)
        try {
            val constructor = storageEnum.clientClass.constructors.first()
            return constructor.call(configId, config)
        } catch (e: Exception) {
            throw IllegalArgumentException("找不到存储器实现构造方法", e)
        }
    }
}