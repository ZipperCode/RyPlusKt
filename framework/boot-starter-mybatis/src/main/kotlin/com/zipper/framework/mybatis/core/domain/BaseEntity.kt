package com.zipper.framework.mybatis.core.domain

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.TableField
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import lombok.Value
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * Entity基类
 *
 * @author Lion Li
 */
abstract class BaseEntity : Serializable {
    /**
     * 搜索值
     */
    @JsonIgnore
    @TableField(exist = false)
    var searchValue: String? = null

    /**
     * 创建部门
     */
    @Deprecated("no used")
    @TableField(fill = FieldFill.INSERT)
    var createDept: Long? = null

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    var createBy: Long? = null

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    var createTime: Date? = null

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    var updateBy: Long? = null

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    var updateTime: Date? = null

    /**
     * 请求参数
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    val params: Map<String, Any> = HashMap()


    companion object {
        @Serial
        var serialVersionUID = 1L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity) return false

        if (searchValue != other.searchValue) return false
        if (createDept != other.createDept) return false
        if (createBy != other.createBy) return false
        if (createTime != other.createTime) return false
        if (updateBy != other.updateBy) return false
        if (updateTime != other.updateTime) return false
        if (params != other.params) return false

        return true
    }

    override fun hashCode(): Int {
        var result = searchValue?.hashCode() ?: 0
        result = 31 * result + (createDept?.hashCode() ?: 0)
        result = 31 * result + (createBy?.hashCode() ?: 0)
        result = 31 * result + (createTime?.hashCode() ?: 0)
        result = 31 * result + (updateBy?.hashCode() ?: 0)
        result = 31 * result + (updateTime?.hashCode() ?: 0)
        result = 31 * result + params.hashCode()
        return result
    }
}
