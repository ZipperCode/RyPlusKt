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
class XcxLoginBody : LoginBody() {
    /**
     * 小程序id(多个小程序时使用)
     */
    var appid: String? = null

    /**
     * 小程序code
     */
    var xcxCode: @NotBlank(message = "{xcx.code.not.blank}") String? = null
}
