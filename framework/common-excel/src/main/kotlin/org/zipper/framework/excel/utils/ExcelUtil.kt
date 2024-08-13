package org.zipper.framework.excel.utils

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.core.util.IdUtil
import com.alibaba.excel.EasyExcel
import com.alibaba.excel.write.metadata.fill.FillConfig
import com.alibaba.excel.write.metadata.fill.FillWrapper
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy
import jakarta.servlet.http.HttpServletResponse
import lombok.AccessLevel
import lombok.NoArgsConstructor
import org.apache.commons.lang3.StringUtils
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.framework.core.utils.StringUtilsExt
import com.zipper.framework.core.utils.file.FileUtilsExt.setAttachmentResponseHeader
import org.zipper.framework.excel.convert.ExcelBigNumberConvert
import org.zipper.framework.excel.core.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException

/**
 * Excel相关处理
 *
 * @author Lion Li
 */
object ExcelUtil {
    /**
     * 同步导入(适用于小数据量)
     *
     * @param is 输入流
     * @return 转换后集合
     */
    fun <T> importExcel(`is`: InputStream?, clazz: Class<T>?): List<T> {
        return EasyExcel.read(`is`).head(clazz).autoCloseStream(false).sheet().doReadSync()
    }


    /**
     * 使用校验监听器 异步导入 同步返回
     *
     * @param is         输入流
     * @param clazz      对象类型
     * @param isValidate 是否 Validator 检验 默认为是
     * @return 转换后集合
     */
    fun <T> importExcel(`is`: InputStream?, clazz: Class<T>?, isValidate: Boolean): ExcelResult<T> {
        val listener = DefaultExcelListener<T>(isValidate)
        EasyExcel.read(`is`, clazz, listener).sheet().doRead()
        return listener.getExcelResult()
    }

    /**
     * 使用自定义监听器 异步导入 自定义返回
     *
     * @param is       输入流
     * @param clazz    对象类型
     * @param listener 自定义监听器
     * @return 转换后集合
     */
    fun <T> importExcel(`is`: InputStream?, clazz: Class<T>?, listener: ExcelListener<T>): ExcelResult<T> {
        EasyExcel.read(`is`, clazz, listener).sheet().doRead()
        return listener.getExcelResult()
    }

    /**
     * 导出excel
     *
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @param clazz     实体类
     * @param response  响应体
     */
    fun <T> exportExcel(list: List<T>, sheetName: String, clazz: Class<T>, response: HttpServletResponse) {
        try {
            resetResponse(sheetName, response)
            val os = response.outputStream
            exportExcel(list, sheetName, clazz, false, os, null)
        } catch (e: IOException) {
            throw RuntimeException("导出Excel异常")
        }
    }

    /**
     * 导出excel
     *
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @param clazz     实体类
     * @param response  响应体
     * @param options   级联下拉选
     */
    fun <T> exportExcel(
        list: List<T>,
        sheetName: String,
        clazz: Class<T>,
        response: HttpServletResponse,
        options: List<DropDownOptions>?
    ) {
        try {
            resetResponse(sheetName, response)
            val os = response.outputStream
            exportExcel(list, sheetName, clazz, false, os, options)
        } catch (e: IOException) {
            throw RuntimeException("导出Excel异常")
        }
    }

    /**
     * 导出excel
     *
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @param clazz     实体类
     * @param merge     是否合并单元格
     * @param response  响应体
     */
    fun <T> exportExcel(list: List<T>, sheetName: String, clazz: Class<T>, merge: Boolean, response: HttpServletResponse) {
        try {
            resetResponse(sheetName, response)
            val os = response.outputStream
            exportExcel(list, sheetName, clazz, merge, os, null)
        } catch (e: IOException) {
            throw RuntimeException("导出Excel异常")
        }
    }

    /**
     * 导出excel
     *
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @param clazz     实体类
     * @param merge     是否合并单元格
     * @param response  响应体
     * @param options   级联下拉选
     */
    fun <T> exportExcel(
        list: List<T>,
        sheetName: String,
        clazz: Class<T>,
        merge: Boolean,
        response: HttpServletResponse,
        options: List<DropDownOptions>?
    ) {
        try {
            resetResponse(sheetName, response)
            val os = response.outputStream
            exportExcel(list, sheetName, clazz, merge, os, options)
        } catch (e: IOException) {
            throw RuntimeException("导出Excel异常")
        }
    }

    /**
     * 导出excel
     *
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @param clazz     实体类
     * @param os        输出流
     */
    fun <T> exportExcel(list: List<T>, sheetName: String?, clazz: Class<T>, os: OutputStream) {
        exportExcel(list, sheetName, clazz, false, os, null)
    }

    /**
     * 导出excel
     *
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @param clazz     实体类
     * @param os        输出流
     * @param options   级联下拉选内容
     */
    fun <T> exportExcel(list: List<T>, sheetName: String?, clazz: Class<T>, os: OutputStream, options: List<DropDownOptions>?) {
        exportExcel(list, sheetName, clazz, false, os, options)
    }

    /**
     * 导出excel
     *
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @param clazz     实体类
     * @param merge     是否合并单元格
     * @param os        输出流
     */
    fun <T> exportExcel(
        list: List<T>, sheetName: String?, clazz: Class<T>, merge: Boolean,
        os: OutputStream, options: List<DropDownOptions>?
    ) {
        val builder = EasyExcel.write(os, clazz)
            .autoCloseStream(false) // 自动适配
            .registerWriteHandler(LongestMatchColumnWidthStyleStrategy()) // 大数值自动转换 防止失真
            .registerConverter(ExcelBigNumberConvert())
            .sheet(sheetName)
        if (merge) {
            // 合并处理器
            builder.registerWriteHandler(CellMergeStrategy(list, true))
        }
        // 添加下拉框操作
        builder.registerWriteHandler(ExcelDownHandler(options ?: emptyList()))
        builder.doWrite(list)
    }

    /**
     * 单表多数据模板导出 模板格式为 {.属性}
     *
     * @param filename     文件名
     * @param templatePath 模板路径 resource 目录下的路径包括模板文件名
     * 例如: excel/temp.xlsx
     * 重点: 模板文件必须放置到启动类对应的 resource 目录下
     * @param data         模板需要的数据
     * @param response     响应体
     */
    fun exportTemplate(data: List<Any?>, filename: String, templatePath: String?, response: HttpServletResponse) {
        try {
            resetResponse(filename, response)
            val os = response.outputStream
            exportTemplate(data, templatePath, os)
        } catch (e: IOException) {
            throw RuntimeException("导出Excel异常")
        }
    }

    /**
     * 单表多数据模板导出 模板格式为 {.属性}
     *
     * @param templatePath 模板路径 resource 目录下的路径包括模板文件名
     * 例如: excel/temp.xlsx
     * 重点: 模板文件必须放置到启动类对应的 resource 目录下
     * @param data         模板需要的数据
     * @param os           输出流
     */
    fun exportTemplate(data: List<Any?>, templatePath: String?, os: OutputStream?) {
        val templateResource = ClassPathResource(templatePath)
        val excelWriter = EasyExcel.write(os)
            .withTemplate(templateResource.stream)
            .autoCloseStream(false) // 大数值自动转换 防止失真
            .registerConverter(ExcelBigNumberConvert())
            .build()
        val writeSheet = EasyExcel.writerSheet().build()
        require(!CollUtil.isEmpty(data)) { "数据为空" }
        // 单表多数据导出 模板格式为 {.属性}
        for (d in data) {
            excelWriter.fill(d, writeSheet)
        }
        excelWriter.finish()
    }

    /**
     * 多表多数据模板导出 模板格式为 {key.属性}
     *
     * @param filename     文件名
     * @param templatePath 模板路径 resource 目录下的路径包括模板文件名
     * 例如: excel/temp.xlsx
     * 重点: 模板文件必须放置到启动类对应的 resource 目录下
     * @param data         模板需要的数据
     * @param response     响应体
     */
    fun exportTemplateMultiList(data: Map<String?, Any?>, filename: String, templatePath: String?, response: HttpServletResponse) {
        try {
            resetResponse(filename, response)
            val os = response.outputStream
            exportTemplateMultiList(data, templatePath, os)
        } catch (e: IOException) {
            throw RuntimeException("导出Excel异常")
        }
    }

    /**
     * 多sheet模板导出 模板格式为 {key.属性}
     *
     * @param filename     文件名
     * @param templatePath 模板路径 resource 目录下的路径包括模板文件名
     * 例如: excel/temp.xlsx
     * 重点: 模板文件必须放置到启动类对应的 resource 目录下
     * @param data         模板需要的数据
     * @param response     响应体
     */
    fun exportTemplateMultiSheet(data: List<Map<String?, Any?>?>, filename: String, templatePath: String?, response: HttpServletResponse) {
        try {
            resetResponse(filename, response)
            val os = response.outputStream
            exportTemplateMultiSheet(data, templatePath, os)
        } catch (e: IOException) {
            throw RuntimeException("导出Excel异常")
        }
    }

    /**
     * 多表多数据模板导出 模板格式为 {key.属性}
     *
     * @param templatePath 模板路径 resource 目录下的路径包括模板文件名
     * 例如: excel/temp.xlsx
     * 重点: 模板文件必须放置到启动类对应的 resource 目录下
     * @param data         模板需要的数据
     * @param os           输出流
     */
    fun exportTemplateMultiList(data: Map<String?, Any?>, templatePath: String?, os: OutputStream?) {
        val templateResource = ClassPathResource(templatePath)
        val excelWriter = EasyExcel.write(os)
            .withTemplate(templateResource.stream)
            .autoCloseStream(false) // 大数值自动转换 防止失真
            .registerConverter(ExcelBigNumberConvert())
            .build()
        val writeSheet = EasyExcel.writerSheet().build()
        require(!CollUtil.isEmpty(data)) { "数据为空" }
        for ((key, value) in data) {
            // 设置列表后续还有数据
            val fillConfig = FillConfig.builder().forceNewRow(java.lang.Boolean.TRUE).build()
            if (value is Collection<*>) {
                // 多表导出必须使用 FillWrapper
                excelWriter.fill(FillWrapper(key, value as Collection<*>?), fillConfig, writeSheet)
            } else {
                excelWriter.fill(value, writeSheet)
            }
        }
        excelWriter.finish()
    }

    /**
     * 多sheet模板导出 模板格式为 {key.属性}
     *
     * @param templatePath 模板路径 resource 目录下的路径包括模板文件名
     * 例如: excel/temp.xlsx
     * 重点: 模板文件必须放置到启动类对应的 resource 目录下
     * @param data         模板需要的数据
     * @param os           输出流
     */
    fun exportTemplateMultiSheet(data: List<Map<String?, Any?>?>, templatePath: String?, os: OutputStream?) {
        val templateResource = ClassPathResource(templatePath)
        val excelWriter = EasyExcel.write(os)
            .withTemplate(templateResource.stream)
            .autoCloseStream(false) // 大数值自动转换 防止失真
            .registerConverter(ExcelBigNumberConvert())
            .build()
        require(!CollUtil.isEmpty(data)) { "数据为空" }
        for (i in data.indices) {
            val writeSheet = EasyExcel.writerSheet(i).build()
            for ((key, value) in data[i]!!) {
                // 设置列表后续还有数据
                val fillConfig = FillConfig.builder().forceNewRow(java.lang.Boolean.TRUE).build()
                if (value is Collection<*>) {
                    // 多表导出必须使用 FillWrapper
                    excelWriter.fill(FillWrapper(key, value as Collection<*>?), fillConfig, writeSheet)
                } else {
                    excelWriter.fill(value, writeSheet)
                }
            }
        }
        excelWriter.finish()
    }

    /**
     * 重置响应体
     */
    @Throws(UnsupportedEncodingException::class)
    private fun resetResponse(sheetName: String, response: HttpServletResponse) {
        val filename = encodingFilename(sheetName)
        setAttachmentResponseHeader(response, filename)
        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"
    }

    /**
     * 解析导出值 0=男,1=女,2=未知
     *
     * @param propertyValue 参数值
     * @param converterExp  翻译注解
     * @param separator     分隔符
     * @return 解析后值
     */
    fun convertByExp(propertyValue: String, converterExp: String, separator: String): String {
        val propertyString = StringBuilder()
        val convertSource: Array<String> = converterExp.split(StringUtilsExt.SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (item in convertSource) {
            val itemArray = item.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (StringUtils.containsAny(propertyValue, separator)) {
                for (value in propertyValue.split(separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    if (itemArray[0] == value) {
                        propertyString.append(itemArray[1] + separator)
                        break
                    }
                }
            } else {
                if (itemArray[0] == propertyValue) {
                    return itemArray[1]
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator)
    }

    /**
     * 反向解析值 男=0,女=1,未知=2
     *
     * @param propertyValue 参数值
     * @param converterExp  翻译注解
     * @param separator     分隔符
     * @return 解析后值
     */
    fun reverseByExp(propertyValue: String, converterExp: String, separator: String): String {
        val propertyString = StringBuilder()
        val convertSource: Array<String> =
            converterExp.split(StringUtilsExt.SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in convertSource) {
            val itemArray = item.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (StringUtils.containsAny(propertyValue, separator)) {
                for (value in propertyValue.split(separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    if (itemArray[1] == value) {
                        propertyString.append(itemArray[0] + separator)
                        break
                    }
                }
            } else {
                if (itemArray[1] == propertyValue) {
                    return itemArray[0]
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator)
    }

    /**
     * 编码文件名
     */
    fun encodingFilename(filename: String): String {
        return IdUtil.fastSimpleUUID() + "_" + filename + ".xlsx"
    }
}
