package com.zipper.framework.core.utils.ip

import cn.hutool.core.net.NetUtil
import cn.hutool.http.HtmlUtil
import org.apache.commons.lang3.StringUtils

/**
 * 获取地址类
 *
 * @author Lion Li
 */
object AddressUtils {
    // 未知地址
    const val UNKNOWN: String = "XX XX"

    @JvmStatic
    fun getRealAddressByIP(ip: String?): String {
        if (StringUtils.isBlank(ip)) {
            return UNKNOWN
        }
        // 内网不查询
        val result = if (StringUtils.contains(ip, "0:0:0:0:0:0:0:1")) "127.0.0.1" else HtmlUtil.cleanHtmlTag(ip)
        if (NetUtil.isInnerIP(result)) {
            return "内网IP"
        }
        return RegionUtils.getCityInfo(result)
    }
}
