package org.zipper.framework.excel.core

import cn.hutool.core.util.StrUtil
import lombok.Setter

/**
 * 默认excel返回对象
 *
 * @author Yjoioooo
 * @author Lion Li
 */
class DefaultExcelResult<T> : ExcelResult<T> {
    /**
     * 数据对象list
     */
    @Setter
    private var list: List<T>

    /**
     * 错误信息列表
     */
    @Setter
    private var errorList: List<String>

    constructor() {
        this.list = ArrayList()
        this.errorList = ArrayList()
    }

    constructor(list: List<T>, errorList: List<String>) {
        this.list = list
        this.errorList = errorList
    }

    constructor(excelResult: ExcelResult<T>) {
        this.list = excelResult.getList()
        this.errorList = excelResult.getErrorList()
    }

    override fun getList(): List<T> {
        return list
    }

    override fun getErrorList(): List<String> {
        return errorList
    }

    /**
     * 获取导入回执
     *
     * @return 导入回执
     */
    override fun getAnalysis(): String {
        val successCount = list.size
        val errorCount = errorList.size
        return if (successCount == 0) {
            "读取失败，未解析到数据"
        } else {
            if (errorCount == 0) {
                StrUtil.format("恭喜您，全部读取成功！共{}条", successCount)
            } else {
                ""
            }
        }
    }
}
