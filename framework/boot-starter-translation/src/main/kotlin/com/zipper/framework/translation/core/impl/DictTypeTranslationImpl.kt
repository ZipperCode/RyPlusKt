package com.zipper.framework.translation.core.impl

import com.zipper.framework.translation.annotation.TranslationType
import com.zipper.framework.translation.constant.TransConstant
import com.zipper.framework.translation.core.TranslationInterface
import org.apache.commons.lang3.StringUtils
import com.zipper.framework.core.service.DictService

/**
 * 字典翻译实现
 *
 * @author Lion Li
 */
@TranslationType(type = TransConstant.DICT_TYPE_TO_LABEL)
class DictTypeTranslationImpl(
    private val dictService: DictService
) : TranslationInterface<String?> {


    override fun translation(key: Any?, other: String?): String? {
        if (key is String && StringUtils.isNotBlank(other)) {
            return dictService.getDictLabel(other!!, key)
        }
        return null
    }
}
