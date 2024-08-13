package org.zipper.framework.excel.convert

import cn.hutool.core.annotation.AnnotationUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import com.alibaba.excel.converters.Converter
import com.alibaba.excel.enums.CellDataTypeEnum
import com.alibaba.excel.metadata.GlobalConfiguration
import com.alibaba.excel.metadata.data.ReadCellData
import com.alibaba.excel.metadata.data.WriteCellData
import com.alibaba.excel.metadata.property.ExcelContentProperty
import lombok.extern.slf4j.Slf4j
import com.zipper.framework.core.utils.reflect.ReflectUtils
import org.zipper.framework.excel.annotation.ExcelEnumFormat
import java.lang.reflect.Field

/**
 * 枚举格式化转换处理
 *
 * @author Liang
 */
@Slf4j
class ExcelEnumConvert : Converter<Any?> {
    override fun supportJavaTypeKey(): Class<Any> = Any::class.java

    override fun supportExcelTypeKey(): CellDataTypeEnum? = null

    override fun convertToJavaData(
        cellData: ReadCellData<*>,
        contentProperty: ExcelContentProperty,
        globalConfiguration: GlobalConfiguration
    ): Any? {
        cellData.checkEmpty()
        // Excel中填入的是枚举中指定的描述
        val textValue: Any = when (cellData.getType()) {
            CellDataTypeEnum.STRING, CellDataTypeEnum.DIRECT_STRING, CellDataTypeEnum.RICH_TEXT_STRING -> cellData.getStringValue()
            CellDataTypeEnum.NUMBER -> cellData.getNumberValue()
            CellDataTypeEnum.BOOLEAN -> cellData.getBooleanValue()
            else -> throw IllegalArgumentException("单元格类型异常!")
        }
        // 如果是空值
        if (ObjectUtil.isNull(textValue)) {
            return null
        }
        val enumCodeToTextMap = beforeConvert(contentProperty)
        // 从Java输出至Excel是code转text
        // 因此从Excel转Java应该将text与code对调
        val enumTextToCodeMap: MutableMap<Any, Any?> = HashMap()
        enumCodeToTextMap.forEach { (key: Any?, value: String) -> enumTextToCodeMap[value] = key }
        // 应该从text -> code中查找
        val codeValue = enumTextToCodeMap[textValue]
        return Convert.convert(contentProperty.getField().getType(), codeValue)
    }

    override fun convertToExcelData(
        `object`: Any?,
        contentProperty: ExcelContentProperty,
        globalConfiguration: GlobalConfiguration
    ): WriteCellData<String> {
        if (ObjectUtil.isNull(`object`)) {
            return WriteCellData<String>("")
        }
        val enumValueMap = beforeConvert(contentProperty)
        val value = Convert.toStr(enumValueMap[`object`], "")
        return WriteCellData<String>(value)
    }

    private fun beforeConvert(contentProperty: ExcelContentProperty): Map<Any?, String> {
        val anno: ExcelEnumFormat = getAnnotation(contentProperty.getField())
        val enumValueMap: MutableMap<Any?, String> = HashMap()
        val enumConstants = anno.enumClass.java.enumConstants
        for (enumConstant in enumConstants) {
            val codeValue = ReflectUtils.invokeGetter<Any>(enumConstant, anno.codeField)
            val textValue = ReflectUtils.invokeGetter<String>(enumConstant, anno.textField)
            enumValueMap[codeValue] = textValue ?: ""
        }
        return enumValueMap
    }

    private fun getAnnotation(field: Field): ExcelEnumFormat {
        return AnnotationUtil.getAnnotation(field, ExcelEnumFormat::class.java)
    }
}
