package org.zipper.framework.excel.core

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ReflectUtil
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.metadata.Head
import com.alibaba.excel.write.merge.AbstractMergeStrategy
import lombok.SneakyThrows
import lombok.extern.slf4j.Slf4j
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellRangeAddress
import com.zipper.framework.core.utils.reflect.ReflectUtils
import org.zipper.framework.excel.annotation.CellMerge
import java.lang.reflect.Field
import kotlin.math.max

/**
 * 列值重复合并策略
 *
 * @author Lion Li
 */
@Slf4j
class CellMergeStrategy(list: List<*>, private val hasTitle: Boolean) : AbstractMergeStrategy() {
    private val cellList: List<CellRangeAddress?>
    private var rowIndex: Int

    init {
        // 行合并开始下标
        this.rowIndex = if (hasTitle) 1 else 0
        this.cellList = handle(list, hasTitle)
    }

    override fun merge(sheet: Sheet, cell: Cell, head: Head, relativeRowIndex: Int) {
        // judge the list is not null
        if (CollUtil.isNotEmpty(cellList)) {
            // the judge is necessary
            if (cell.rowIndex == rowIndex && cell.columnIndex == 0) {
                for (item in cellList) {
                    sheet.addMergedRegion(item)
                }
            }
        }
    }

    @SneakyThrows
    private fun handle(list: List<*>, hasTitle: Boolean): List<CellRangeAddress?> {
        val cellList: MutableList<CellRangeAddress?> = ArrayList()
        if (list.isEmpty()) {
            return cellList
        }
        val fields: Array<Field> = ReflectUtil.getFields(list[0]!!.javaClass) { field: Field ->
            "serialVersionUID" != field.name
        }

        // 有注解的字段
        val mergeFields: MutableList<Field> = ArrayList()
        val mergeFieldsIndex: MutableList<Int> = ArrayList()
        for (i in fields.indices) {
            val field = fields[i]
            if (field.isAnnotationPresent(CellMerge::class.java)) {
                val cm = field.getAnnotation(CellMerge::class.java)
                mergeFields.add(field)
                mergeFieldsIndex.add(if (cm.index == -1) i else cm.index)
                if (hasTitle) {
                    val property = field.getAnnotation(ExcelProperty::class.java)
                    rowIndex = max(rowIndex.toDouble(), property.value.size.toDouble()).toInt()
                }
            }
        }

        val map: MutableMap<Field, RepeatCell> = HashMap()
        // 生成两两合并单元格
        for (i in list.indices) {
            for (j in mergeFields.indices) {
                val field = mergeFields[j]
                val value = ReflectUtils.invokeGetter<Any>(list[i]!!, field.name)

                val colNum = mergeFieldsIndex[j]
                if (!map.containsKey(field)) {
                    map[field] = RepeatCell(value, i)
                } else {
                    val repeatCell = map[field]
                    val cellValue = repeatCell?.value ?: continue
                    if ("" == cellValue) {
                        // 空值跳过不合并
                        continue
                    }
                    if (cellValue != value) {
                        if (i - repeatCell.current > 1) {
                            cellList.add(CellRangeAddress(repeatCell.current + rowIndex, i + rowIndex - 1, colNum, colNum))
                        }
                        map[field] = RepeatCell(value, i)
                    } else if (j == 0) {
                        if (i == list.size - 1) {
                            if (i > repeatCell.current) {
                                cellList.add(CellRangeAddress(repeatCell.current + rowIndex, i + rowIndex, colNum, colNum))
                            }
                        }
                    } else {
                        // 判断前面的是否合并了
                        val firstCell = map[mergeFields[0]] ?: continue
                        if (repeatCell.current != firstCell.current) {
                            if (i == list.size - 1) {
                                if (i > repeatCell.current) {
                                    cellList.add(CellRangeAddress(repeatCell.current + rowIndex, i + rowIndex, colNum, colNum))
                                }
                            } else if (repeatCell.current < firstCell.current) {
                                if (i - repeatCell.current > 1) {
                                    cellList.add(CellRangeAddress(repeatCell.current + rowIndex, i + rowIndex - 1, colNum, colNum))
                                }
                                map[field] = RepeatCell(value, i)
                            }
                        } else if (i == list.size - 1) {
                            if (i > repeatCell.current) {
                                cellList.add(CellRangeAddress(repeatCell.current + rowIndex, i + rowIndex, colNum, colNum))
                            }
                        }
                    }
                }
            }
        }
        return cellList
    }


    internal data class RepeatCell(
        val value: Any? = null,
        val current: Int = 0
    )
}
