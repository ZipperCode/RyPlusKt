package com.zipper.modules.storage.domain.entity

import com.baomidou.mybatisplus.annotation.*
import com.zipper.framework.core.annotation.NoArgs
import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.framework.mybatis.core.domain.BaseEntity2
import com.zipper.framework.mybatis.core.domain.CreatorMixin
import com.zipper.modules.storage.domain.mixin.FileRecordMixin
import lombok.Data
import lombok.EqualsAndHashCode
import java.time.LocalDateTime

/**
 * 文件上传记录 对象 store_file_record
 */
@Data
@NoArgs
@TableName("sys_file_record", autoResultMap = true)
class SysFileRecordEntity : FileRecordMixin, CreatorMixin {
    @TableId(value = "record_id", type = IdType.ASSIGN_ID)
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
     * 文件大小
     */
    override var fileSize: Long = 0

    /**
     * 文件hash值
     */
    override var hash: String? = null

    /**
     * 服务商
     */
    override var service: String? = null

    @field:TableField(fill = FieldFill.INSERT)
    override var createBy: String? = ""

    @field:TableField(fill = FieldFill.INSERT)
    override var createTime: LocalDateTime? = null
}