package org.zipper.framework.excel.convert

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import com.alibaba.excel.converters.Converter
import com.alibaba.excel.enums.CellDataTypeEnum
import com.alibaba.excel.metadata.GlobalConfiguration
import com.alibaba.excel.metadata.data.ReadCellData
import com.alibaba.excel.metadata.data.WriteCellData
import com.alibaba.excel.metadata.property.ExcelContentProperty
import java.math.BigDecimal

/**
 * 大数值转换
 * Excel 数值长度位15位 大于15位的数值转换位字符串
 *
 * @author Lion Li
 */
class ExcelBigNumberConvert : Converter<Long> {
    override fun supportJavaTypeKey(): Class<Long> = Long::class.java

    override fun supportExcelTypeKey(): CellDataTypeEnum = CellDataTypeEnum.STRING

    override fun convertToJavaData(
        cellData: ReadCellData<*>,
        contentProperty: ExcelContentProperty,
        globalConfiguration: GlobalConfiguration
    ): Long = Convert.toLong(cellData.data)

    override fun convertToExcelData(
        value: Long,
        contentProperty: ExcelContentProperty,
        globalConfiguration: GlobalConfiguration
    ): WriteCellData<Any> {
        if (ObjectUtil.isNotNull(value)) {
            val str = Convert.toStr(value)
            if (str.length > 15) {
                return WriteCellData(str)
            }
        }
        val cellData = WriteCellData<Any>(BigDecimal(value))
        cellData.type = CellDataTypeEnum.NUMBER
        return cellData
    }
}
