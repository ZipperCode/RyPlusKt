package com.zipper.modules.system.domain.vo

import lombok.Data

/**
 * 上传对象信息
 *
 * @author Michelle.Chung
 */
@Data
class SysOssUploadVo {
    /**
     * URL地址
     */
    var url: String? = null

    /**
     * 文件名
     */
    var fileName: String? = null

    /**
     * 对象存储主键
     */
    var ossId: String? = null
}
