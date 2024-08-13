package com.zipper.framework.email.utils

import cn.hutool.core.util.ArrayUtil
import jakarta.mail.internet.AddressException
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeUtility
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

/**
 * 邮件内部工具类
 *
 * @author looly
 * @since 3.2.3
 */
object InternalMailUtil {
    /**
     * 将多个字符串邮件地址转为[InternetAddress]列表<br></br>
     * 单个字符串地址可以是多个地址合并的字符串
     *
     * @param addrStrs 地址数组
     * @param charset  编码（主要用于中文用户名的编码）
     * @return 地址数组
     * @since 4.0.3
     */
    @JvmStatic
    fun parseAddressFromStrs(addrStrs: Array<out String?>, charset: Charset?): Array<InternetAddress> {
        val resultList = ArrayList<InternetAddress>(addrStrs.size)
        var addrs: Array<InternetAddress>
        for (addrStr in addrStrs) {
            addrs = parseAddress(addrStr, charset)
            if (ArrayUtil.isNotEmpty<InternetAddress>(addrs)) {
                Collections.addAll(resultList, *addrs)
            }
        }
        return resultList.toTypedArray<InternetAddress>()
    }

    /**
     * 解析第一个地址
     *
     * @param address 地址字符串
     * @param charset 编码，`null`表示使用系统属性定义的编码或系统编码
     * @return 地址列表
     */
    @JvmStatic
    fun parseFirstAddress(address: String?, charset: Charset?): InternetAddress {
        val internetAddresses = parseAddress(address, charset)
        if (ArrayUtil.isEmpty(internetAddresses)) {
            try {
                return InternetAddress(address)
            } catch (e: AddressException) {
                throw MailException(e)
            }
        }
        return internetAddresses[0]
    }

    /**
     * 将一个地址字符串解析为多个地址<br></br>
     * 地址间使用" "、","、";"分隔
     *
     * @param address 地址字符串
     * @param charset 编码，`null`表示使用系统属性定义的编码或系统编码
     * @return 地址列表
     */
    fun parseAddress(address: String?, charset: Charset?): Array<InternetAddress> {
        val addresses: Array<InternetAddress>
        try {
            addresses = InternetAddress.parse(address)
        } catch (e: AddressException) {
            throw MailException(e)
        }
        //编码用户名
        if (ArrayUtil.isNotEmpty(addresses)) {
            val charsetStr = charset?.name()
            for (internetAddress in addresses) {
                try {
                    internetAddress.setPersonal(internetAddress.personal, charsetStr)
                } catch (e: UnsupportedEncodingException) {
                    throw MailException(e)
                }
            }
        }

        return addresses
    }

    /**
     * 编码中文字符<br></br>
     * 编码失败返回原字符串
     *
     * @param text    被编码的文本
     * @param charset 编码
     * @return 编码后的结果
     */
    @JvmStatic
    fun encodeText(text: String, charset: Charset): String {
        try {
            return MimeUtility.encodeText(text, charset.name(), null)
        } catch (e: UnsupportedEncodingException) {
            // ignore
        }
        return text
    }
}
