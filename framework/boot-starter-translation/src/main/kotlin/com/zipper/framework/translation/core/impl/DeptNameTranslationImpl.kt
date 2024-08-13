package com.zipper.framework.translation.core.impl

import com.zipper.framework.translation.annotation.TranslationType
import com.zipper.framework.translation.constant.TransConstant
import com.zipper.framework.translation.core.TranslationInterface
import com.zipper.framework.core.service.DeptService

/**
 * 部门翻译实现
 *
 * @author Lion Li
 */
@TranslationType(type = TransConstant.DEPT_ID_TO_NAME)
class DeptNameTranslationImpl(
    private val deptService: DeptService
) : TranslationInterface<String?> {


    override fun translation(key: Any?, other: String?): String? {
        if (key is String) {
            return deptService.selectDeptNameByIds(key)
        } else if (key is Long) {
            return deptService.selectDeptNameByIds(key.toString())
        }
        return null
    }
}
