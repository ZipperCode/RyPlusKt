package com.zipper.framework.mybatis.core.page

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.core.metadata.OrderItem
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import org.apache.commons.lang3.StringUtils
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.StringUtilsExt
import com.zipper.framework.core.utils.sql.SqlUtil
import java.io.Serial
import java.io.Serializable

/**
 * 分页查询实体类
 *
 * @author Lion Li
 */
class PageQuery : Serializable {
    /**
     * 分页大小
     */
    var pageSize: Int = DEFAULT_PAGE_SIZE

    /**
     * 当前页数
     */
    var pageNum: Int = DEFAULT_PAGE_NUM

    /**
     * 排序列
     */
    var orderByColumn: String? = null

    /**
     * 排序的方向desc或者asc
     */
    var isAsc: String? = null

    fun <T> build(): Page<T> {
        if (pageNum <= 0) {
            pageNum = DEFAULT_PAGE_NUM
        }
        val page = Page<T>(pageNum.toLong(), pageSize.toLong())
        val orderItems = buildOrderItem()
        if (CollUtil.isNotEmpty(orderItems)) {
            page.addOrder(orderItems)
        }
        return page
    }

    /**
     * 构建排序
     *
     * 支持的用法如下:
     * {isAsc:"asc",orderByColumn:"id"} order by id asc
     * {isAsc:"asc",orderByColumn:"id,createTime"} order by id asc,create_time asc
     * {isAsc:"desc",orderByColumn:"id,createTime"} order by id desc,create_time desc
     * {isAsc:"asc,desc",orderByColumn:"id,createTime"} order by id asc,create_time desc
     */
    private fun buildOrderItem(): List<OrderItem>? {
        if (orderByColumn.isNullOrEmpty() || isAsc.isNullOrEmpty()) {
            return null
        }
        var orderBy = SqlUtil.escapeOrderBySql(orderByColumn!!)
        orderBy = StrUtil.toUnderlineCase(orderBy)

        // 兼容前端排序类型
        isAsc = StringUtils.replaceEach(isAsc, arrayOf("ascending", "descending"), arrayOf("asc", "desc"))

        val orderByArr = orderBy.split(SEPARATOR)
        val isAscArr = isAsc!!.split(SEPARATOR)
        if (isAscArr.size != 1 && isAscArr.size != orderByArr.size) {
            throw ServiceException("排序参数有误")
        }

        val list: MutableList<OrderItem> = ArrayList()
        // 每个字段各自排序
        for (i in orderByArr.indices) {
            val orderByStr = orderByArr[i]
            val isAscStr = if (isAscArr.size == 1) isAscArr[0] else isAscArr[i]
            when (isAscStr) {
                "asc" -> {
                    list.add(OrderItem.asc(orderByStr))
                }
                "desc" -> {
                    list.add(OrderItem.desc(orderByStr))
                }
                else -> {
                    throw ServiceException("排序参数有误")
                }
            }
        }
        return list
    }

    companion object {
        @Serial
        var serialVersionUID = 1L

        /**
         * 当前记录起始索引 默认值
         */
        const val DEFAULT_PAGE_NUM: Int = 1

        /**
         * 每页显示记录数 默认值 默认查全部
         */
        const val DEFAULT_PAGE_SIZE: Int = Int.MAX_VALUE

        const val SEPARATOR = ","
    }
}
