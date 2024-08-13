package com.zipper.framework.core.utils.file

/**
 * 媒体类型工具类
 *
 * @author ruoyi
 */
object MimeTypeUtils {
    const val IMAGE_PNG: String = "image/png"

    const val IMAGE_JPG: String = "image/jpg"

    const val IMAGE_JPEG: String = "image/jpeg"

    const val IMAGE_BMP: String = "image/bmp"

    const val IMAGE_GIF: String = "image/gif"

    val IMAGE_EXTENSION: Array<String> = arrayOf("bmp", "gif", "jpg", "jpeg", "png")

    val FLASH_EXTENSION: Array<String> = arrayOf("swf", "flv")

    val MEDIA_EXTENSION: Array<String> = arrayOf(
        "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg",
        "asf", "rm", "rmvb"
    )

    val VIDEO_EXTENSION: Array<String> = arrayOf("mp4", "avi", "rmvb")

    val DEFAULT_ALLOWED_EXTENSION: Array<String> = arrayOf( // 图片
        "bmp", "gif", "jpg", "jpeg", "png",  // word excel powerpoint
        "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",  // 压缩文件
        "rar", "zip", "gz", "bz2",  // 视频格式
        "mp4", "avi", "rmvb",  // pdf
        "pdf"
    )
}
