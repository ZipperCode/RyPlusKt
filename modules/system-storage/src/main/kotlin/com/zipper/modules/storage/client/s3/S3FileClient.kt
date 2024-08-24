package com.zipper.modules.storage.client.s3

import com.zipper.framework.oss.core.OssClient
import com.zipper.framework.oss.core.OssConfig
import com.zipper.framework.oss.factory.OssFactory
import com.zipper.modules.storage.client.BaseFileClient

class S3FileClient(
    id: Long, config: S3FileClientConfig
) : BaseFileClient<S3FileClientConfig>(id, config) {

    private val client: OssClient

    init {
        val ossConfig = OssConfig(
            config.endpoint,
            config.getCustomDomain(),
            config.accessKey,
            config.accessSecret,
            config.bucket,
            config.region,
            config.prefix,
            config.useHttps,
            config.accessPolicy
        )
        val cacheKey = "${getId()}_${config.service}"
        client = OssFactory.getInstance(cacheKey, ossConfig)
    }

    override fun upload(content: ByteArray, path: String, type: String): String {
        val uploadData = client.upload(content, path, type)
        return uploadData.url
    }

    override fun delete(path: String) {
        client.delete(path)
    }

    override fun getContent(path: String): ByteArray {
        return client.getObjectContent(path).readBytes()
    }
}