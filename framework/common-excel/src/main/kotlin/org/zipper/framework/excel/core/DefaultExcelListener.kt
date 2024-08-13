package org.zipper.framework.excel.core

import cn.hutool.core.util.StrUtil
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.event.AnalysisEventListener
import com.alibaba.excel.exception.ExcelAnalysisException
import com.alibaba.excel.exception.ExcelDataConvertException
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.StreamUtils
import com.zipper.framework.core.utils.ValidatorUtils

/**
 * Excel 导入监听
 *
 * @author Yjoioooo
 * @author Lion Li
 */
class DefaultExcelListener<T>(isValidate: Boolean) : AnalysisEventListener<T>(), ExcelListener<T> {
    /**
     * 是否Validator检验，默认为是
     */
    private var isValidate: Boolean = java.lang.Boolean.TRUE

    /**
     * excel 表头数据
     */
    private var headMap: Map<Int, String>? = null

    /**
     * 导入回执
     */
    private val excelResult: ExcelResult<T> = DefaultExcelResult()

    init {
        this.isValidate = isValidate
    }

    /**
     * 处理异常
     *
     * @param exception ExcelDataConvertException
     * @param context   Excel 上下文
     */
    @Throws(Exception::class)
    override fun onException(exception: Exception, context: AnalysisContext) {
        var errMsg: String? = null
        if (exception is ExcelDataConvertException) {
            // 如果是某一个单元格的转换异常 能获取到具体行号
            val rowIndex: Int = exception.getRowIndex()
            val columnIndex: Int = exception.getColumnIndex()
            errMsg = StrUtil.format(
                "第{}行-第{}列-表头{}: 解析异常<br/>",
                rowIndex + 1, columnIndex + 1, headMap!![columnIndex]
            )
            if (log.isDebugEnabled()) {
                log.error(errMsg)
            }
        }
        if (exception is ConstraintViolationException) {
            val constraintViolations: Set<ConstraintViolation<*>> = exception.getConstraintViolations()
            val constraintViolationsMsg: String =
                StreamUtils.join(constraintViolations, { obj: ConstraintViolation<*> -> obj.getMessage() }, ", ")
            errMsg = StrUtil.format("第{}行数据校验异常: {}", context.readRowHolder().rowIndex + 1, constraintViolationsMsg)
            if (log.isDebugEnabled()) {
                log.error(errMsg)
            }
        }
        if (errMsg != null) {
            excelResult.getErrorList().toMutableList().add(errMsg)
        }
        throw ExcelAnalysisException(errMsg)
    }

    override fun invokeHeadMap(headMap: Map<Int, String>, context: AnalysisContext) {
        this.headMap = headMap
        // log.debug("解析到一条表头数据: {}", JsonUtils.toJsonString(headMap))
    }

    override fun invoke(data: T, context: AnalysisContext) {
        if (isValidate) {
            ValidatorUtils.validate(data)
        }
        excelResult.getList().toMutableList().add(data)
    }

    override fun doAfterAllAnalysed(context: AnalysisContext) {
        log.debug("所有数据解析完成！")
    }

    override fun getExcelResult(): ExcelResult<T> {
        return excelResult
    }
}
