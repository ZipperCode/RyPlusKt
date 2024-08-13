package com.zipper.framework.core.domain.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 邮件登录对象
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
class EmailLoginBody : LoginBody() {
    /**
     * 邮箱
     */
    var email: @NotBlank(message = "{user.email.not.blank}") @Email(message = "{user.email.not.valid}") String? = null

    /**
     * 邮箱code
     */
    var emailCode: @NotBlank(message = "{email.code.not.blank}") String? = null
}
