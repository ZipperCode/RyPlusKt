package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.framework.sensitive.annotation.Sensitive
import com.zipper.framework.sensitive.core.SensitiveStrategy
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import lombok.Data
import lombok.NoArgsConstructor
import com.zipper.framework.core.xss.Xss
import lombok.EqualsAndHashCode

/**
 * 个人信息业务处理
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
class SysUserProfileBo : BaseEntity() {
    /**
     * 用户ID
     */
    var userId: Long? = null

    /**
     * 用户昵称
     */
    @field:Xss(message = "用户昵称不能包含脚本字符")
    @field:Size(min = 0, max = 30, message = "用户昵称长度不能超过{max}个字符")
    var nickName: String? = null

    /**
     * 用户邮箱
     */
    @field:Sensitive(strategy = SensitiveStrategy.EMAIL)
    @field:Email(message = "邮箱格式不正确")
    @field:Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    var email: String? = null

    /**
     * 手机号码
     */
    @field:Sensitive(strategy = SensitiveStrategy.PHONE)
    var phonenumber: String? = null

    /**
     * 用户性别（0男 1女 2未知）
     */
    var sex: String? = null
}
