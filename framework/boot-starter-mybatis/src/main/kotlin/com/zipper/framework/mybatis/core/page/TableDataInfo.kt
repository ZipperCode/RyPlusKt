package com.zipper.framework.mybatis.core.page

import cn.hutool.http.HttpStatus
import com.baomidou.mybatisplus.core.metadata.IPage
import java.io.Serial
import java.io.Serializable

/**
 * 表格分页数据对象
 *
 * @param rows  列表数据
 * @param total 总记录数
 */
class TableDataInfo<T>(
    /**
     * 列表数据
     */
    val rows: List<T> = emptyList(),
    /**
     * 总记录数
     */
    val total: Long = 0,
    /**
     * 消息状态码
     */
    val code: Int = 0,

    /**
     * 消息内容
     */
    val msg: String? = null
) : Serializable {


    companion object {
        @Serial
        private val serialVersionUID = 1L

        fun <T> build(page: IPage<T>): TableDataInfo<T> {
            return TableDataInfo<T>(page.records, page.total, HttpStatus.HTTP_OK, "查询成功")
        }

        fun <T> build(list: List<T>): TableDataInfo<T> {
            return TableDataInfo<T>(list, list.size.toLong(), HttpStatus.HTTP_OK, "查询成功")
        }

        fun <T> build(): TableDataInfo<T> {
            return TableDataInfo<T>(code = HttpStatus.HTTP_OK, msg = "查询成功")
        }
    }
}
