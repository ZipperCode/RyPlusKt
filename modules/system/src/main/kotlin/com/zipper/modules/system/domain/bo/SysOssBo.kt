package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysOssEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * OSS对象存储分页查询对象 sys_oss
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysOssEntity::class, reverseConvertGenerate = false)
class SysOssBo : BaseEntity() {
    /**
     * ossId
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
    var url: String? = null

    /**
     * 服务商
     */
    var service: String? = null
}
