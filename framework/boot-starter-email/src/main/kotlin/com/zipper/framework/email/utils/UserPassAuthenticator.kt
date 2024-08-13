package com.zipper.framework.email.utils

import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication

/**
 * 用户名密码验证器
 *  * @param user 用户名
 *  * @param pass 密码
 */
class UserPassAuthenticator(private val user: String?, private val pass: String?) : Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(this.user, this.pass)
    }
}
