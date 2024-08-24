package com.zipper.modules.storage.service

import com.zipper.modules.storage.domain.bo.FileCreateBo
import com.zipper.modules.storage.domain.entity.SysFileConfigEntity
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import kotlin.jvm.Throws

/**
 * 文件服务
 */
interface FileService {
    /**
     * 创建文件
     * @param relativePath 自定义的保存路径 如 /20240816/123.png
     */
    fun createFile(multipartFile: MultipartFile, relativePath: String?): FileCreateBo

    /**
     * 删除文件
     * @param configId [SysFileConfigEntity.id]
     * @param relativePath 相对路径
     * @throws Exception 删除文件时，抛出 Exception 异常
     */
    fun deleteFile(configId: Long, relativePath: String)

    /**
     * 获取文件内容
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getFileContent(configId: Long, relativePath: String): ByteArray
}