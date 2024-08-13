package com.zipper.framework.core.domain.model

import jakarta.validation.constraints.NotBlank
import lombok.Data
import java.io.Serial
import java.io.Serializable

/**
 * 用户登录对象
 *
 * @author Lion Li
 */
@Data
open class LoginBody : Serializable {
    /**
     * 客户端id
     */
    var clientId: @NotBlank(message = "{auth.clientid.not.blank}") String? = null

    /**
     * 授权类型
     */
    var grantType: @NotBlank(message = "{auth.grant.type.not.blank}") String = ""

    /**
     * 租户ID
     */
    var tenantId: String? = null

    /**
     * 验证码
     */
    var code: String? = null

    /**
     * 唯一标识
     */
    var uuid: String? = null

    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
