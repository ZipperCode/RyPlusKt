package com.zipper.modules.storage.domain.mixin

import com.zipper.modules.storage.domain.entity.SysFileConfigEntity

interface FileRecordMixin {

    /**
     * 关联[SysFileConfigEntity.id]
     */
    var configId: Long?

    /**
     * 文件相对路径
     */
    var path: String?

    /**
     * 访问地址，上传完成后得到的
     */
    var url: String?

    /**
     * 上传的文件类型
     */
    var mimeType: String?

    /**
     * 文件大小
     */
    var fileSize: Long

    /**
     * 文件hash值
     */
    var hash: String?

    /**
     * 服务商
     */
    var service: String?
}