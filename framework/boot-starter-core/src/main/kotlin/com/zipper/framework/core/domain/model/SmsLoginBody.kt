package com.zipper.framework.core.domain.model

import jakarta.validation.constraints.NotBlank
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 短信登录对象
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
class SmsLoginBody : LoginBody() {
    /**
     * 手机号
     */
    var phonenumber: @NotBlank(message = "{user.phonenumber.not.blank}") String? = null

    /**
     * 短信code
     */
    var smsCode: @NotBlank(message = "{sms.code.not.blank}") String? = null
}
