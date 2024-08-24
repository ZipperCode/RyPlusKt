package com.zipper.modules.storage.domain.param

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

@Schema(description = "管理后台 - 上传文件")
class FileUploadParam {
    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull(message = "文件附件不能为空")
    lateinit var file: MultipartFile

    /**
     * 相对路径
     * 不传自动生成
     */
    @Schema(description = "", example = "/relative/path1/path2")
    var relativePath: String? = null
}