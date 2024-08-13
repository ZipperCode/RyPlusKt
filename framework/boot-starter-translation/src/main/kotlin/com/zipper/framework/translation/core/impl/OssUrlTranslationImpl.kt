package com.zipper.framework.translation.core.impl

import com.zipper.framework.translation.annotation.TranslationType
import com.zipper.framework.translation.constant.TransConstant
import com.zipper.framework.translation.core.TranslationInterface
import com.zipper.framework.core.service.OssService

/**
 * OSS翻译实现
 *
 * @author Lion Li
 */
@TranslationType(type = TransConstant.OSS_ID_TO_URL)
class OssUrlTranslationImpl(
    private val ossService: OssService
) : TranslationInterface<String?> {

    override fun translation(key: Any?, other: String?): String? {
        if (key is String) {
            return ossService.selectUrlByIds(key)
        } else if (key is Long) {
            return ossService.selectUrlByIds(key.toString())
        }
        return null
    }
}
