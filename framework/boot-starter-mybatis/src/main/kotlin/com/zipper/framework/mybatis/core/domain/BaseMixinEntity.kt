package com.zipper.framework.mybatis.core.domain

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableLogic
import lombok.EqualsAndHashCode
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*
@EqualsAndHashCode
abstract class BaseMixinEntity : CreatorMixin, UpdaterMixin, Serializable {
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

    /**
     * 更新者
     */
    @field:TableField(fill = FieldFill.INSERT_UPDATE)
    override var updateBy: String? = null

    /**
     * 更新时间
     */
    @field:TableField(fill = FieldFill.INSERT_UPDATE)
    override var updateTime: LocalDateTime? = null
}