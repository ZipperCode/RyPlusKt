package com.zipper.framework.core.utils.file

import cn.hutool.core.io.FileMagicNumber
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.StrUtil
import jakarta.servlet.http.HttpServletResponse
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 文件处理工具类
 *
 * @author Lion Li
 */
object FileUtilsExt : FileUtil() {

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     */
    @JvmStatic
    fun setAttachmentResponseHeader(response: HttpServletResponse, realFileName: String?) {
        val percentEncodedFileName = percentEncode(realFileName)
        val contentDispositionValue =
            "attachment; filename=%s;filename*=utf-8''%s".formatted(percentEncodedFileName, percentEncodedFileName)
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename")
        response.setHeader("Content-disposition", contentDispositionValue)
        response.setHeader("download-filename", percentEncodedFileName)
    }

    /**
     * 返回附件
     *
     * @param response 响应
     * @param filename 文件名
     * @param content  附件内容
     */
    @Throws(IOException::class)
    fun writeAttachment(response: HttpServletResponse, filename: String?, content: ByteArray) {
        // 设置 header 和 contentType
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"))
        val contentType = getFileType(content, filename).mimeType
        response.contentType = contentType
        // 针对 video 的特殊处理，解决视频地址在移动端播放的兼容性问题
        if (StrUtil.containsIgnoreCase(contentType, "video")) {
            response.setHeader("Content-Length", (content.size - 1).toString())
            response.setHeader("Content-Range", (content.size - 1).toString())
            response.setHeader("Accept-Ranges", "bytes")
        }
        // 输出附件
        IoUtil.write(response.outputStream, false, content)
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    @JvmStatic
    fun percentEncode(s: String?): String {
        val encode = URLEncoder.encode(s, StandardCharsets.UTF_8)
        return encode.replace("\\+".toRegex(), "%20")
    }

    /**
     * 简单扩展文件类型
     * 后续需要判断精确，可以使用tika库判断
     */
    fun getFileType(inputStream: InputStream, filename: String?, readHeadSize: Int = 64): FileMagicNumber {
        val buffer = BufferedInputStream(inputStream)
        buffer.mark(readHeadSize)
        val magicNumber = IoUtil.readBytes(inputStream, readHeadSize)
        buffer.reset()
        val fileMagic = FileMagicNumber.getMagicNumber(magicNumber)
        return validFileType(fileMagic, filename)
    }

    fun getFileType(bytes: ByteArray, filename: String?, readHeadSize: Int = 64): FileMagicNumber {
        val fileMagic = FileMagicNumber.getMagicNumber(bytes)
        return validFileType(fileMagic, filename)
    }

    private fun validFileType(fileMagic: FileMagicNumber, filename: String?): FileMagicNumber {
        if (fileMagic.extension == "zip" || fileMagic.extension == "jar") {
            val extName = FileUtil.extName(filename)
            when (extName) {
                "docx" -> return FileMagicNumber.DOCX
                "xlsx" -> return FileMagicNumber.XLSX
                "pptx" -> return FileMagicNumber.PPTX
            }
        }
        return fileMagic
    }
}
