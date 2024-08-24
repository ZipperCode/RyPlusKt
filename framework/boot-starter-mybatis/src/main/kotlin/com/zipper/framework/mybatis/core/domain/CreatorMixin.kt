package com.zipper.framework.mybatis.core.domain

import java.io.Serializable
import java.time.LocalDateTime

interface CreatorMixin : Serializable {
    /**
     * 创建者
     */
    var createBy: String?

    /**
     * 创建时间
     */
    var createTime: LocalDateTime?
}