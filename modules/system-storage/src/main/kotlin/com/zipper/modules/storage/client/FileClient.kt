package com.zipper.modules.storage.client


/**
 * 文件客户端
 *
 * @author 芋道源码
 */
interface FileClient {
    /**
     * 获得客户端编号
     *
     * @return 客户端编号
     */
    fun getId(): Long

    fun getConfig(): FileClientConfig

    /**
     * 上传文件
     *
     * @param content 文件流
     * @param path    相对路径
     * @return 完整路径，即 HTTP 访问地址
     * @throws Exception 上传文件时，抛出 Exception 异常
     */
    @Throws(Exception::class)
    fun upload(content: ByteArray, path: String, type: String): String

    /**
     * 删除文件
     *
     * @param path 相对路径
     * @throws Exception 删除文件时，抛出 Exception 异常
     */
    @Throws(Exception::class)
    fun delete(path: String)

    /**
     * 获得文件的内容
     *
     * @param path 相对路径
     * @return 文件的内容
     */
    @Throws(Exception::class)
    fun getContent(path: String): ByteArray
    /**
     * 获得文件预签名地址
     *
     * @param path 相对路径
     * @return 文件预签名地址
     */
    //    default FilePresignedUrlRespDTO getPresignedObjectUrl(String path) throws Exception {
    //        throw new UnsupportedOperationException("不支持的操作");
    //    }
}
