package com.zipper.modules.storage.domain.entity

import com.baomidou.mybatisplus.annotation.*
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler
import com.zipper.framework.json.utils.JsonUtils
import com.zipper.framework.json.utils.JsonUtils.createReference
import com.zipper.framework.mybatis.core.domain.BaseMixinEntity
import com.zipper.framework.mybatis.core.domain.LogicDeleteMixin
import com.zipper.modules.storage.client.FileClientConfig
import com.zipper.modules.storage.client.db.DataBaseFileClientConfig
import com.zipper.modules.storage.client.local.LocalFileClientConfig
import com.zipper.modules.storage.client.s3.S3FileClientConfig

@TableName("sys_file_config", autoResultMap = true)
class SysFileConfigEntity : BaseMixinEntity(), LogicDeleteMixin {
    /**
     * 配置编号，数据库自增
     */
    @TableId(value = "config_id", type = IdType.AUTO)
    var id: Long? = null

    val requireConfigId: Long get() = id!!

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

    val requireStorage: Int get() = storage!!

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

    @TableField(typeHandler = FileClientConfigTypeHandler::class)
    lateinit var config: FileClientConfig

    @field:TableLogic
    override var deleted: Int = LogicDeleteMixin.NORMAL

    class FileClientConfigTypeHandler : AbstractJsonTypeHandler<FileClientConfig>() {
        override fun parse(json: String): FileClientConfig {
            return when (val typeValue = JsonUtils.parseObject(json, "@class", String::class.java)) {
                DataBaseFileClientConfig::class.java.name -> {
                    JsonUtils.parseObjectReference(json, createReference<DataBaseFileClientConfig>())
                }

                LocalFileClientConfig::class.java.name -> {
                    JsonUtils.parseObjectReference(json, createReference<LocalFileClientConfig>())
                }

                S3FileClientConfig::class.java.name -> {
                    JsonUtils.parseObjectReference(json, createReference<S3FileClientConfig>())
                }

                else -> {
                    throw IllegalArgumentException("不支持的配置类型: $typeValue")
                }
            }
        }

        override fun toJson(obj: FileClientConfig): String = JsonUtils.toJson(obj)
    }
}