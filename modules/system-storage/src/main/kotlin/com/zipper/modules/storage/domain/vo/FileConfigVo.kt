package com.zipper.modules.storage.domain.vo

import com.fasterxml.jackson.annotation.JsonIgnoreType
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.zipper.modules.storage.client.FileClientConfig
import com.zipper.modules.storage.domain.entity.SysFileConfigEntity
import io.github.linpeilie.annotations.AutoMapper
import java.time.LocalDateTime

@JsonIgnoreType
@AutoMapper(target = SysFileConfigEntity::class)
class FileConfigVo {

    /**
     * 配置编号，数据库自增
     */
    var id: Long? = null

    /**
     * 配置名
     */
    var name: String? = null

    /**
     * 存储器
     *
     * 枚举 [FileStorageEnum]
     */
    var storage: Int? = null

    /**
     * 备注
     */
    var remark: String? = null

    /**
     * 是否为主配置
     *
     * 由于我们可以配置多个文件配置，默认情况下，使用主配置进行文件的上传
     */
    var master: Boolean = false

    /**
     * 配置
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    var config: FileClientConfig? = null

    var createTime: LocalDateTime? = null

    var createBy: String? = null
}