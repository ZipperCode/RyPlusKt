package com.zipper.framework.email.utils

import cn.hutool.core.io.IORuntimeException

/**
 * 全局邮件帐户，依赖于邮件配置文件[MailAccount.MAIL_SETTING_PATHS]
 *
 * @author looly
 */
object GlobalMailAccount {

    /**
     * 获得邮件帐户
     *
     * @return 邮件帐户
     */
    val account: MailAccount? by lazy {
        createDefaultAccount()
    }


    /**
     * 创建默认帐户
     *
     * @return MailAccount
     */
    private fun createDefaultAccount(): MailAccount? {
        for (mailSettingPath in MailAccount.MAIL_SETTING_PATHS) {
            try {
                return MailAccount(mailSettingPath)
            } catch (ignore: IORuntimeException) {
                //ignore
            }
        }
        return null
    }
}
