package com.zipper.framework.translation.core.impl

import com.zipper.framework.translation.annotation.TranslationType
import com.zipper.framework.translation.constant.TransConstant
import com.zipper.framework.translation.core.TranslationInterface
import lombok.AllArgsConstructor
import com.zipper.framework.core.service.UserService

/**
 * 用户名称翻译实现
 *
 * @author may
 */
@TranslationType(type = TransConstant.USER_ID_TO_NICKNAME)
class NicknameTranslationImpl(
    private val userService: UserService
) : TranslationInterface<String?> {

    override fun translation(key: Any?, other: String?): String? {
        if (key is Long) {
            return userService.selectNicknameById(key)!!
        }
        return null
    }
}
