package org.zipper.framework.excel.core

/**
 * excel返回对象
 *
 * @author Lion Li
 */
interface ExcelResult<T> {
    /**
     * 对象列表
     */
    fun getList(): List<T>

    /**
     * 错误列表
     */
    fun getErrorList(): List<String>

    /**
     * 导入回执
     */
    fun getAnalysis(): String
}
