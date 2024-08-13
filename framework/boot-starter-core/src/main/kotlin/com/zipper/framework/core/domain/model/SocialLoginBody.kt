package com.zipper.framework.core.domain.model

import jakarta.validation.constraints.NotBlank
import lombok.Data
import lombok.EqualsAndHashCode

/**
 * 三方登录对象
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
class SocialLoginBody : LoginBody() {
    /**
     * 第三方登录平台
     */
    var source: @NotBlank(message = "{social.source.not.blank}") String = ""

    /**
     * 第三方登录code
     */
    var socialCode: @NotBlank(message = "{social.code.not.blank}") String = ""

    /**
     * 第三方登录socialState
     */
    var socialState: @NotBlank(message = "{social.state.not.blank}") String = ""
}
