package com.zipper.framework.encrypt.enums

/**
 * 编码类型
 *
 * @author 老马
 * @version 4.6.0
 */
enum class EncodeType {
    /**
     * 默认使用yml配置
     */
    DEFAULT,

    /**
     * base64编码
     */
    BASE64,

    /**
     * 16进制编码
     */
    HEX;

    fun isDefault() = this == DEFAULT
}
