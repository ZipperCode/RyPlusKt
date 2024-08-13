package org.zipper.framework.excel.core

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.EnumUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.extra.spring.SpringUtil
import com.alibaba.excel.util.ClassUtils
import com.alibaba.excel.write.handler.SheetWriteHandler
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder
import lombok.extern.slf4j.Slf4j
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddressList
import org.apache.poi.ss.util.WorkbookUtil
import org.apache.poi.xssf.usermodel.XSSFDataValidation
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.service.DictService
import com.zipper.framework.core.utils.StreamUtils
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.annotation.ExcelEnumFormat
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * <h1>Excel表格下拉选操作</h1>
 * 考虑到下拉选过多可能导致Excel打开缓慢的问题，只校验前1000行
 *
 *
 * 即只有前1000行的数据可以用下拉框，超出的自行通过限制数据量的形式，第二次输出
 *
 * @author Emil.Zhang
 */
@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ExcelDownHandler(
    /**
     * 下拉可选项
     */
    private val dropDownOptions: List<DropDownOptions>
) : SheetWriteHandler {
    /**
     * 当前单选进度
     */
    private var currentOptionsColumnIndex = 0

    /**
     * 当前联动选择进度
     */
    private var currentLinkedOptionsSheetIndex = 0
    private val dictService: DictService by lazy {
        SpringUtil.getBean(DictService::class.java)
    }

    /**
     * <h2>开始创建下拉数据</h2>
     * 1.通过解析传入的@ExcelProperty同级是否标注有@DropDown选项
     * 如果有且设置了value值，则将其直接置为下拉可选项
     *
     *
     * 2.或者在调用ExcelUtil时指定了可选项，将依据传入的可选项做下拉
     *
     *
     * 3.二者并存，注意调用方式
     */
    override fun afterSheetCreate(writeWorkbookHolder: WriteWorkbookHolder, writeSheetHolder: WriteSheetHolder) {
        val sheet: Sheet = writeSheetHolder.getSheet()
        // 开始设置下拉框 HSSFWorkbook
        val helper: DataValidationHelper = sheet.dataValidationHelper
        val workbook: Workbook = writeWorkbookHolder.getWorkbook()
        val fieldCache = ClassUtils.declaredFields(writeWorkbookHolder.getClazz(), writeWorkbookHolder)
        for ((index, wrapper) in fieldCache.sortedFieldMap) {
            val field = wrapper.field
            // 循环实体中的每个属性
            // 可选的下拉值
            var options: List<String?> = ArrayList()
            if (field.isAnnotationPresent(ExcelDictFormat::class.java)) {
                // 如果指定了@ExcelDictFormat，则使用字典的逻辑
                val format: ExcelDictFormat = field.getDeclaredAnnotation(ExcelDictFormat::class.java)
                val dictType: String = format.dictType
                val converterExp: String = format.readConverterExp
                if (StrUtil.isNotBlank(dictType)) {
                    // 如果传递了字典名，则依据字典建立下拉
                    val values: Collection<String?> = Optional.ofNullable(dictService.getAllDictByDictType(dictType))
                        .orElseThrow<RuntimeException>{
                            ServiceException(
                                String.format(
                                    "字典 %s 不存在",
                                    dictType
                                )
                            )
                        }
                        .values
                    options = ArrayList(values)
                } else if (StrUtil.isNotBlank(converterExp)) {
                    // 如果指定了确切的值，则直接解析确切的值
                    options = StrUtil.split(converterExp, format.separator, true, true)
                }
            } else if (field.isAnnotationPresent(ExcelEnumFormat::class.java)) {
                // 否则如果指定了@ExcelEnumFormat，则使用枚举的逻辑
                val format: ExcelEnumFormat = field.getDeclaredAnnotation(ExcelEnumFormat::class.java)
                val values = EnumUtil.getFieldValues(format.enumClass.java, format.textField)
                options = StreamUtils.toList(values, java.lang.String::valueOf)
            }
            if (ObjectUtil.isNotEmpty(options)) {
                // 仅当下拉可选项不为空时执行
                if (options.size > 20) {
                    // 这里限制如果可选项大于20，则使用额外表形式
                    dropDownWithSheet(helper, workbook, sheet, index, options)
                } else {
                    // 否则使用固定值形式
                    dropDownWithSimple(helper, sheet, index, options)
                }
            }
        }
        if (CollUtil.isEmpty(dropDownOptions)) {
            return
        }
        dropDownOptions.forEach { everyOptions: DropDownOptions ->
            // 如果传递了下拉框选择器参数
            if (everyOptions.nextOptions.isNotEmpty()) {
                // 当二级选项不为空时，使用额外关联表的形式
                dropDownLinkedOptions(helper, workbook, sheet, everyOptions)
            } else if (everyOptions.options.size > 10) {
                // 当一级选项参数个数大于10，使用额外表的形式
                dropDownWithSheet(helper, workbook, sheet, everyOptions.index, everyOptions.options)
            } else if (everyOptions.options.isNotEmpty()) {
                // 当一级选项个数不为空，使用默认形式
                dropDownWithSimple(helper, sheet, everyOptions.index, everyOptions.options)
            }
        }
    }

    /**
     * <h2>简单下拉框</h2>
     * 直接将可选项拼接为指定列的数据校验值
     *
     * @param celIndex 列index
     * @param value    下拉选可选值
     */
    private fun dropDownWithSimple(helper: DataValidationHelper, sheet: Sheet, celIndex: Int, value: List<String?>) {
        if (ObjectUtil.isEmpty(value)) {
            return
        }
        this.markOptionsToSheet(helper, sheet, celIndex, helper.createExplicitListConstraint(ArrayUtil.toArray(value, String::class.java)))
    }

    /**
     * <h2>额外表格形式的级联下拉框</h2>
     *
     * @param options 额外表格形式存储的下拉可选项
     */
    private fun dropDownLinkedOptions(helper: DataValidationHelper, workbook: Workbook, sheet: Sheet, options: DropDownOptions) {
        val linkedOptionsSheetName = String.format("%s_%d", LINKED_OPTIONS_SHEET_NAME, currentLinkedOptionsSheetIndex)
        // 创建联动下拉数据表
        val linkedOptionsDataSheet: Sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(linkedOptionsSheetName))
        // 将下拉表隐藏
        workbook.setSheetHidden(workbook.getSheetIndex(linkedOptionsDataSheet), true)
        // 完善横向的一级选项数据表
        val firstOptions: List<String> = options.options
        val secoundOptionsMap: Map<String, List<String?>> = options.nextOptions

        // 创建名称管理器
        val name: Name = workbook.createName()
        // 设置名称管理器的别名
        name.nameName = linkedOptionsSheetName
        // 以横向第一行创建一级下拉拼接引用位置
        val firstOptionsFunction = String.format(
            "%s!$%s$1:$%s$1",
            linkedOptionsSheetName,
            getExcelColumnName(0),
            getExcelColumnName(firstOptions.size)
        )
        // 设置名称管理器的引用位置
        name.refersToFormula = firstOptionsFunction
        // 设置数据校验为序列模式，引用的是名称管理器中的别名
        this.markOptionsToSheet(helper, sheet, options.index, helper.createFormulaListConstraint(linkedOptionsSheetName))

        for (columIndex in firstOptions.indices) {
            // 先提取主表中一级下拉的列名
            val firstOptionsColumnName = getExcelColumnName(columIndex)
            // 一次循环是每一个一级选项
            val finalI = columIndex
            // 本次循环的一级选项值
            val thisFirstOptionsValue = firstOptions[columIndex]
            // 创建第一行的数据
            Optional.ofNullable(linkedOptionsDataSheet.getRow(0)) // 如果不存在则创建第一行
                .orElseGet { linkedOptionsDataSheet.createRow(finalI) } // 第一行当前列
                .createCell(columIndex) // 设置值为当前一级选项值
                .setCellValue(thisFirstOptionsValue)

            // 第二行开始，设置第二级别选项参数
            var secondOptions = secoundOptionsMap[thisFirstOptionsValue]!!
            if (CollUtil.isEmpty(secondOptions)) {
                // 必须保证至少有一个关联选项，否则将导致Excel解析错误
                secondOptions = listOf("暂无_0")
            }

            // 以该一级选项值创建子名称管理器
            val sonName: Name = workbook.createName()
            // 设置名称管理器的别名
            sonName.nameName = thisFirstOptionsValue
            // 以第二行该列数据拼接引用位置
            val sonFunction = String.format(
                "%s!$%s$2:$%s$%d",
                linkedOptionsSheetName,
                firstOptionsColumnName,
                firstOptionsColumnName,
                secondOptions.size + 1
            )
            // 设置名称管理器的引用位置
            sonName.refersToFormula = sonFunction
            // 数据验证为序列模式，引用到每一个主表中的二级选项位置
            // 创建子项的名称管理器，只是为了使得Excel可以识别到数据
            val mainSheetFirstOptionsColumnName = getExcelColumnName(options.index)
            for (i in 0..99) {
                // 以一级选项对应的主体所在位置创建二级下拉
                val secondOptionsFunction = String.format("=INDIRECT(%s%d)", mainSheetFirstOptionsColumnName, i + 1)
                // 二级只能主表每一行的每一列添加二级校验
                markLinkedOptionsToSheet(helper, sheet, i, options.nextIndex,
                    helper.createFormulaListConstraint(secondOptionsFunction)
                )
            }

            for (rowIndex in secondOptions.indices) {
                // 从第二行开始填充二级选项
                val finalRowIndex = rowIndex + 1
                val finalColumIndex = columIndex

                val row = Optional.ofNullable(linkedOptionsDataSheet.getRow(finalRowIndex)) // 没有则创建
                    .orElseGet { linkedOptionsDataSheet.createRow(finalRowIndex) }
                Optional // 在本级一级选项所在的列
                    .ofNullable(row.getCell(finalColumIndex)) // 不存在则创建
                    .orElseGet { row.createCell(finalColumIndex) } // 设置二级选项值
                    .setCellValue(secondOptions[rowIndex])
            }
        }

        currentLinkedOptionsSheetIndex++
    }

    /**
     * <h2>额外表格形式的普通下拉框</h2>
     * 由于下拉框可选值数量过多，为提升Excel打开效率，使用额外表格形式做下拉
     *
     * @param celIndex 下拉选
     * @param value    下拉选可选值
     */
    private fun dropDownWithSheet(helper: DataValidationHelper, workbook: Workbook, sheet: Sheet, celIndex: Int, value: List<String?>) {
        // 创建下拉数据表
        val simpleDataSheet = Optional.ofNullable<Sheet>(workbook.getSheet(WorkbookUtil.createSafeSheetName(OPTIONS_SHEET_NAME)))
            .orElseGet { workbook.createSheet(WorkbookUtil.createSafeSheetName(OPTIONS_SHEET_NAME)) }
        // 将下拉表隐藏
        workbook.setSheetHidden(workbook.getSheetIndex(simpleDataSheet), true)
        // 完善纵向的一级选项数据表
        for (i in value.indices) {
            val finalI = i
            // 获取每一选项行，如果没有则创建
            val row = Optional.ofNullable(simpleDataSheet.getRow(i))
                .orElseGet { simpleDataSheet.createRow(finalI) }
            // 获取本级选项对应的选项列，如果没有则创建
            val cell = Optional.ofNullable(row.getCell(currentOptionsColumnIndex))
                .orElseGet { row.createCell(currentOptionsColumnIndex) }
            // 设置值
            cell.setCellValue(value[i])
        }

        // 创建名称管理器
        val name: Name = workbook.createName()
        // 设置名称管理器的别名
        val nameName = String.format("%s_%d", OPTIONS_SHEET_NAME, celIndex)
        name.nameName = nameName
        // 以纵向第一列创建一级下拉拼接引用位置
        val function = String.format(
            "%s!$%s$1:$%s$%d",
            OPTIONS_SHEET_NAME,
            getExcelColumnName(currentOptionsColumnIndex),
            getExcelColumnName(currentOptionsColumnIndex),
            value.size
        )
        // 设置名称管理器的引用位置
        name.refersToFormula = function
        // 设置数据校验为序列模式，引用的是名称管理器中的别名
        this.markOptionsToSheet(helper, sheet, celIndex, helper.createFormulaListConstraint(nameName))
        currentOptionsColumnIndex++
    }

    /**
     * 挂载下拉的列，仅限一级选项
     */
    private fun markOptionsToSheet(
        helper: DataValidationHelper, sheet: Sheet, celIndex: Int,
        constraint: DataValidationConstraint
    ) {
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        val addressList = CellRangeAddressList(1, 1000, celIndex, celIndex)
        markDataValidationToSheet(helper, sheet, constraint, addressList)
    }

    /**
     * 挂载下拉的列，仅限二级选项
     */
    private fun markLinkedOptionsToSheet(
        helper: DataValidationHelper, sheet: Sheet, rowIndex: Int,
        celIndex: Int, constraint: DataValidationConstraint
    ) {
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        val addressList: CellRangeAddressList = CellRangeAddressList(rowIndex, rowIndex, celIndex, celIndex)
        markDataValidationToSheet(helper, sheet, constraint, addressList)
    }

    /**
     * 应用数据校验
     */
    private fun markDataValidationToSheet(
        helper: DataValidationHelper, sheet: Sheet,
        constraint: DataValidationConstraint, addressList: CellRangeAddressList
    ) {
        // 数据有效性对象
        val dataValidation: DataValidation = helper.createValidation(constraint, addressList)
        // 处理Excel兼容性问题
        if (dataValidation is XSSFDataValidation) {
            //数据校验
            dataValidation.setSuppressDropDownArrow(true)
            //错误提示
            dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP)
            dataValidation.createErrorBox("提示", "此值与单元格定义数据不一致")
            dataValidation.setShowErrorBox(true)
            //选定提示
            dataValidation.createPromptBox("填写说明：", "填写内容只能为下拉中数据，其他数据将导致导入失败")
            dataValidation.setShowPromptBox(true)
            sheet.addValidationData(dataValidation)
        } else {
            dataValidation.setSuppressDropDownArrow(false)
        }
        sheet.addValidationData(dataValidation)
    }

    /**
     * <h2>依据列index获取列名英文</h2>
     * 依据列index转换为Excel中的列名英文
     *
     * 例如第1列，index为0，解析出来为A列
     * 第27列，index为26，解析为AA列
     *
     * 第28列，index为27，解析为AB列
     *
     * @param columnIndex 列index
     * @return 列index所在得英文名
     */
    private fun getExcelColumnName(columnIndex: Int): String {
        // 26一循环的次数
        val columnCircleCount = columnIndex / 26
        // 26一循环内的位置
        val thisCircleColumnIndex = columnIndex % 26
        // 26一循环的次数大于0，则视为栏名至少两位
        val columnPrefix = if (columnCircleCount == 0
        ) StrUtil.EMPTY
        else StrUtil.subWithLength(EXCEL_COLUMN_NAME, columnCircleCount - 1, 1)
        // 从26一循环内取对应的栏位名
        val columnNext = StrUtil.subWithLength(EXCEL_COLUMN_NAME, thisCircleColumnIndex, 1)
        // 将二者拼接即为最终的栏位名
        return columnPrefix + columnNext
    }

    companion object {
        /**
         * Excel表格中的列名英文
         * 仅为了解析列英文，禁止修改
         */
        private const val EXCEL_COLUMN_NAME = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        /**
         * 单选数据Sheet名
         */
        private const val OPTIONS_SHEET_NAME = "options"

        /**
         * 联动选择数据Sheet名的头
         */
        private const val LINKED_OPTIONS_SHEET_NAME = "linkedOptions"
    }
}
