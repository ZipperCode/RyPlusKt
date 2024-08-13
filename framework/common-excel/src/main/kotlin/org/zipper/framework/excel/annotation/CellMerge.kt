package org.zipper.framework.excel.annotation

import java.lang.annotation.Inherited


/**
 * excel 列单元格合并(合并列相同项)
 *
 * 需搭配 [CellMergeStrategy] 策略使用
 *
 * @author Lion Li
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class CellMerge(
    /**
     * col index
     */
    val index: Int = -1
)
