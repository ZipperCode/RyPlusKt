package com.zipper.modules.storage.service

import cn.hutool.core.io.FileMagicNumber
import cn.hutool.core.io.FileTypeUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.crypto.digest.DigestUtil
import com.zipper.framework.core.utils.file.FileUtilsExt
import com.zipper.framework.core.utils.ktext.withType
import com.zipper.modules.storage.client.s3.S3FileClient
import com.zipper.modules.storage.client.s3.S3FileClientConfig
import com.zipper.modules.storage.domain.bo.FileCreateBo
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream
import kotlin.math.min

/**
 * 文件服务，用于操作文件上传，删除，获取
 */
@Service
class FileServiceImpl(
    private val fileConfigService: FileConfigService
) : FileService {

    override fun createFile(multipartFile: MultipartFile, relativePath: String?): FileCreateBo {
        val stream = BufferedInputStream(multipartFile.inputStream)
        stream.mark(min(stream.available(), 1024))
        val originFileName: String? = multipartFile.originalFilename ?: multipartFile.name
        val type = FileUtilsExt.getFileType(stream, originFileName)
        if (type == FileMagicNumber.UNKNOWN) {
            throw RuntimeException("文件类型未知")
        }
        stream.reset()
        val fileSize = multipartFile.size
        val extName = originFileName?.substringAfter(".") ?: type.extension
        val bytes = IoUtil.readBytes(stream)
        val hash = DigestUtil.sha256Hex(bytes)
        var path = "$hash.$extName"
        // 自定义路径需要后缀相同
        if (!relativePath.isNullOrEmpty() && relativePath.endsWith(extName)) {
            path = relativePath.replace("../", "/")
        }
        val masterFileClient = fileConfigService.getMasterFileClient()
        val configId = masterFileClient.getId()
        val url = masterFileClient.upload(bytes, path, type.mimeType)
        var serviceProvider: String? = null
        masterFileClient.getConfig().withType<S3FileClientConfig> {
            serviceProvider = service
        }
        return FileCreateBo(configId, path, url, type.mimeType, hash, fileSize, serviceProvider, extName)
    }

    override fun deleteFile(configId: Long, relativePath: String) {
        val fileClient = fileConfigService.getFileClient(configId)
        fileClient.delete(relativePath)
    }


    override fun getFileContent(configId: Long, relativePath: String): ByteArray {
        val fileClient = fileConfigService.getFileClient(configId)
        return fileClient.getContent(relativePath)
    }
}