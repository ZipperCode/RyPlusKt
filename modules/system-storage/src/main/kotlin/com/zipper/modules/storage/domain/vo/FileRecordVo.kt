package com.zipper.modules.storage.domain.vo

import com.zipper.modules.storage.domain.entity.SysFileConfigEntity
import com.zipper.modules.storage.domain.entity.SysFileRecordEntity
import com.zipper.modules.storage.domain.mixin.FileRecordMixin
import io.github.linpeilie.annotations.AutoMapper
import java.time.LocalDateTime

@AutoMapper(target = SysFileRecordEntity::class)
class FileRecordVo: FileRecordMixin {

    var id: Long? = null

    /**
     * 关联[SysFileConfigEntity.id]
     */
    override var configId: Long? = null

    /**
     * 文件相对路径
     */
    override var path: String? = null

    /**
     * 访问地址，上传完成后得到的
     */
    override var url: String? = null

    /**
     * 上传的文件类型
     */
    override var mimeType: String? = null

    /**
     * 文件hash
     */
    override var hash: String? = null

    /**
     * 存储服务商
     */
    override var service: String? = null
    /**
     * 文件大小
     */
    override var fileSize: Long = 0

    var createBy: String? = null

    var createTime: LocalDateTime? = null
}