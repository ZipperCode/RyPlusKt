package com.zipper.modules.storage.domain.entity

import com.baomidou.mybatisplus.annotation.*
import com.zipper.framework.core.annotation.NoArgs
import com.zipper.framework.mybatis.core.domain.CreatorMixin
import com.zipper.framework.mybatis.core.domain.LogicDeleteMixin
import lombok.Data
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Data
@NoArgs
@TableName("sys_file_content", autoResultMap = true)
class SysFileContentEntity : CreatorMixin, LogicDeleteMixin {

    /**
     * 编号
     */
    @TableId("file_id", type = IdType.ASSIGN_UUID)
    var id: String? = null

    /**
     * 配置编号
     *
     * 关联 [SysFileConfigEntity.id]
     */
    var configId: Long = 0

    /**
     * 路径，即文件名
     */
    lateinit var path: String

    /**
     * 文件内容
     */
    lateinit var content: ByteArray

    /**
     * 创建者
     */
    @field:TableField(fill = FieldFill.INSERT)
    override var createBy: String? = null

    /**
     * 创建时间
     */
    @field:TableField(fill = FieldFill.INSERT)
    override var createTime: LocalDateTime? = null

    @field:TableLogic
    override var deleted: Int = LogicDeleteMixin.NORMAL
}