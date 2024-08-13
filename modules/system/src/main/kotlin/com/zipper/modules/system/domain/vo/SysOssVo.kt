package com.zipper.modules.system.domain.vo

import com.zipper.framework.translation.annotation.Translation
import com.zipper.framework.translation.constant.TransConstant
import com.zipper.modules.system.domain.entity.SysOssEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * OSS对象存储视图对象 sys_oss
 *
 * @author Lion Li
 */
@Data
@AutoMapper(target = SysOssEntity::class)
class SysOssVo : Serializable {
    /**
     * 对象存储主键
     */
    var ossId: Long? = null

    /**
     * 文件名
     */
    var fileName: String? = null

    /**
     * 原名
     */
    var originalName: String? = null

    /**
     * 文件后缀名
     */
    var fileSuffix: String? = null

    /**
     * URL地址
     */
    var url: String = ""

    /**
     * 创建时间
     */
    var createTime: Date? = null

    /**
     * 上传人
     */
    var createBy: Long? = null

    /**
     * 上传人名称
     */
    @field:Translation(type = TransConstant.USER_ID_TO_NAME, mapper = "createBy")
    var createByName: String? = null

    /**
     * 服务商
     */
    var service: String = ""
    
}
