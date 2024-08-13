package com.zipper.framework.core.domain.model

import lombok.Data
import lombok.EqualsAndHashCode
import lombok.NoArgsConstructor
import java.io.Serial

/**
 * 小程序登录用户身份权限
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
class XcxLoginUser : LoginUser() {
    /**
     * openid
     */
    var openid: String? = null

    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
