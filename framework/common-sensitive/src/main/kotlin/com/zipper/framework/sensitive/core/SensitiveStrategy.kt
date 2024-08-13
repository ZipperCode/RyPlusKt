package com.zipper.framework.sensitive.core

import cn.hutool.core.util.DesensitizedUtil
import java.util.function.Function

/**
 * 脱敏策略
 *
 * @author Yjoioooo
 * @version 3.6.0
 */
enum class SensitiveStrategy(
    val desensitizer: Function<String, String>
) {
    /**
     * 身份证脱敏
     */
    ID_CARD(Function { s: String? -> DesensitizedUtil.idCardNum(s, 3, 4) }),

    /**
     * 手机号脱敏
     */
    PHONE(Function { num: String? -> DesensitizedUtil.mobilePhone(num) }),

    /**
     * 地址脱敏
     */
    ADDRESS(Function { s: String? -> DesensitizedUtil.address(s, 8) }),

    /**
     * 邮箱脱敏
     */
    EMAIL(Function { email: String? -> DesensitizedUtil.email(email) }),

    /**
     * 银行卡
     */
    BANK_CARD(Function { bankCardNo: String? -> DesensitizedUtil.bankCard(bankCardNo) }); //可自行添加其他脱敏策略
}
