package com.zipper.framework.encrypt.core

import com.zipper.framework.encrypt.annotation.EncryptField
import com.zipper.framework.encrypt.enums.AlgorithmType
import com.zipper.framework.encrypt.enums.EncodeType
import com.zipper.framework.encrypt.config.properties.EncryptorProperties

/**
 * 加密上下文 用于encryptor传递必要的参数。
 *
 * @author 老马
 * @version 4.6.0
 */
class EncryptContext(
    encryptField: com.zipper.framework.encrypt.annotation.EncryptField,
    defaultProperties: com.zipper.framework.encrypt.config.properties.EncryptorProperties
) {
    /**
     * 默认算法
     */
    val algorithm: AlgorithmType = if (encryptField.algorithm.isDefault()) defaultProperties.algorithm else encryptField.algorithm

    /**
     * 安全秘钥
     */
    val password: String = encryptField.password.ifBlank { defaultProperties.password }

    /**
     * 公钥
     */
    val publicKey: String = encryptField.publicKey.ifBlank { defaultProperties.publicKey }

    /**
     * 私钥
     */
    val privateKey: String = encryptField.privateKey.ifBlank { defaultProperties.privateKey }

    /**
     * 编码方式，base64/hex
     */
    val encode: EncodeType = if (encryptField.encode.isDefault()) defaultProperties.encode else encryptField.encode
}
