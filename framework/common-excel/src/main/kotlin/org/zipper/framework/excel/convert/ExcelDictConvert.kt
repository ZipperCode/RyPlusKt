package org.zipper.framework.excel.convert

import cn.hutool.core.annotation.AnnotationUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import cn.hutool.extra.spring.SpringUtil
import com.alibaba.excel.converters.Converter
import com.alibaba.excel.enums.CellDataTypeEnum
import com.alibaba.excel.metadata.GlobalConfiguration
import com.alibaba.excel.metadata.data.ReadCellData
import com.alibaba.excel.metadata.data.WriteCellData
import com.alibaba.excel.metadata.property.ExcelContentProperty
import org.apache.commons.lang3.StringUtils
import com.zipper.framework.core.service.DictService
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.utils.ExcelUtil
import java.lang.reflect.Field

/**
 * 字典格式化转换处理
 *
 * @author Lion Li
 */
class ExcelDictConvert : Converter<Any> {
    override fun supportJavaTypeKey(): Class<Any> = Any::class.java

    override fun supportExcelTypeKey(): CellDataTypeEnum? = null

    override fun convertToJavaData(
        cellData: ReadCellData<*>,
        contentProperty: ExcelContentProperty,
        globalConfiguration: GlobalConfiguration
    ): Any {
        val anno = getAnnotation(contentProperty.field)
        val type: String = anno.dictType
        val label: String = cellData.stringValue
        val value: String = if (StringUtils.isBlank(type)) {
            ExcelUtil.reverseByExp(label, anno.readConverterExp, anno.separator)
        } else {
            SpringUtil.getBean(DictService::class.java).getDictValue(type, label, anno.separator)
        }
        return Convert.convert(contentProperty.getField().getType(), value)
    }

    override fun convertToExcelData(
        value: Any,
        contentProperty: ExcelContentProperty,
        globalConfiguration: GlobalConfiguration
    ): WriteCellData<String> {
        if (ObjectUtil.isNull(value)) {
            return WriteCellData<String>("")
        }
        val anno: ExcelDictFormat = getAnnotation(contentProperty.field)
        val type: String = anno.dictType
        val str = Convert.toStr(value)
        val label: String = if (StringUtils.isBlank(type)) {
            ExcelUtil.convertByExp(str, anno.readConverterExp, anno.separator)
        } else {
            SpringUtil.getBean(DictService::class.java).getDictLabel(type, str, anno.separator)
        }
        return WriteCellData<String>(label)
    }

    private fun getAnnotation(field: Field): ExcelDictFormat {
        return AnnotationUtil.getAnnotation(field, ExcelDictFormat::class.java)
    }
}
