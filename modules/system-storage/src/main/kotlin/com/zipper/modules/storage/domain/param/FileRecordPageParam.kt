package com.zipper.modules.storage.domain.param

import com.zipper.framework.mybatis.core.page.PageQuery
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

/**
 * 文件记录分页查询
 */
@Schema(description = "管理后台 - 上传文件记录")
class FileRecordPageParam : PageQuery() {
    @Schema(description = "文件路径，模糊匹配", example = "图片")
    var path: String? = null

    @Schema(description = "文件类型，模糊匹配", example = "jpg")
    var type: String? = null

    @Schema(description = "创建时间", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var createTime: Array<LocalDateTime>? = null
}