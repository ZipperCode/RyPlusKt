package com.zipper.modules.storage.domain.param

import com.zipper.modules.storage.domain.entity.SysFileConfigEntity
import io.github.linpeilie.annotations.AutoMapper
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "管理后台 - 文件创建 Request VO")
@AutoMapper(target = SysFileConfigEntity::class)
class FileRecordSaveParam {

    /**
     * 关联[SysFileConfigEntity.id]
     */
    @field:NotNull(message = "文件配置编号不能为空")
    @Schema(description = "文件配置编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    var configId: Long? = null

    /**
     * 文件相对路径
     */
    @NotNull(message = "文件路径不能为空")
    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao.jpg")
    var path: String? = null

    /**
     * 访问地址，上传完成后得到的
     */
    @NotNull(message = "文件 URL不能为空")
    @Schema(description = "文件 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/yudao.jpg")
    var url: String? = null

    /**
     * 上传的文件类型
     */
    @Schema(description = "文件 MIME 类型", example = "application/octet-stream")
    var mimeType: String? = null

    /**
     * 文件大小
     */
    @Schema(description = "文件大小", example = "2048", requiredMode = Schema.RequiredMode.REQUIRED)
    var fileSize: Long = 0
}