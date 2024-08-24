package com.zipper.modules.storage.domain.bo

import com.zipper.framework.core.annotation.NoArgs
import com.zipper.modules.storage.enums.FileStorageEnum
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "管理后台 - 文件配置分页 Request VO")
open class FileConfigBo {
    @field:Schema(description = "配置名", example = "S3 - 阿里云")
    var name: String? = null

    /**
     * 存储器
     *
     * 枚举 [FileStorageEnum]
     */
    @field:Schema(description = "存储器", example = "1")
    var storage: Int? = null

    /**
     * 备注
     */
    @field:Schema(description = "备注", example = "1")
    var remark: String? = null
}