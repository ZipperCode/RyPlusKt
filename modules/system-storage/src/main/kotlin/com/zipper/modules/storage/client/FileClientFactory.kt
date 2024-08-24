package com.zipper.modules.storage.client


interface FileClientFactory {
    /**
     * 获得文件客户端
     *
     * @param configId 配置编号
     * @return 文件客户端
     */
    fun getFileClient(configId: Long): FileClient?

    /**
     * 创建文件客户端
     *
     * @param configId 配置编号
     * @param storage 存储器的枚举 [FileStorageEnum]
     * @param config 文件配置
     */
    fun <Config : FileClientConfig> createFileClient(configId: Long, storage: Int, config: Config): FileClient


    fun <Config : FileClientConfig> updateFileClientIfExists(configId: Long, storage: Int, config: Config)

}
