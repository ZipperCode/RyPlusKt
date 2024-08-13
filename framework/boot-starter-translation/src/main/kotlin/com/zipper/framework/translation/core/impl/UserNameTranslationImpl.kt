package com.zipper.framework.translation.core.impl

import com.zipper.framework.translation.annotation.TranslationType
import com.zipper.framework.translation.constant.TransConstant
import com.zipper.framework.translation.core.TranslationInterface
import com.zipper.framework.core.service.UserService

/**
 * 用户名翻译实现
 *
 * @author Lion Li
 */
@TranslationType(type = TransConstant.USER_ID_TO_NAME)
class UserNameTranslationImpl(
    private val userService: UserService
) : TranslationInterface<String?> {

    override fun translation(key: Any?, other: String?): String? {
        if (key is Long) {
            return userService.selectUserNameById(key)
        }
        return null
    }
}
