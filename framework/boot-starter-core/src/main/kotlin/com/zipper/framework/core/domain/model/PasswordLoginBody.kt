package com.zipper.framework.core.domain.model

import com.zipper.framework.core.constant.UserConstants
import jakarta.validation.constraints.NotBlank
import lombok.Data
import lombok.EqualsAndHashCode
import org.hibernate.validator.constraints.Length

/**
 * 密码登录对象
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
class PasswordLoginBody : LoginBody() {
    /**
     * 用户名
     */
    var username: @NotBlank(message = "{user.username.not.blank}") @Length(
        min = UserConstants.USERNAME_MIN_LENGTH,
        max = UserConstants.USERNAME_MAX_LENGTH,
        message = "{user.username.length.valid}"
    ) String? = null

    /**
     * 用户密码
     */
    var password: @NotBlank(message = "{user.password.not.blank}") @Length(
        min = UserConstants.PASSWORD_MIN_LENGTH,
        max = UserConstants.PASSWORD_MAX_LENGTH,
        message = "{user.password.length.valid}"
    ) String? = null
}
