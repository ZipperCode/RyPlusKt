package com.zipper.framework.encrypt.core.encryptor

import org.apache.commons.lang3.StringUtils
import com.zipper.framework.encrypt.core.EncryptContext
import com.zipper.framework.encrypt.enums.AlgorithmType
import com.zipper.framework.encrypt.enums.EncodeType
import com.zipper.framework.encrypt.utils.EncryptUtils

/**
 * RSA算法实现
 *
 * @author 老马
 * @version 4.6.0
 */
class RsaEncryptor(context: com.zipper.framework.encrypt.core.EncryptContext) : AbstractEncryptor(context) {

    init {
        val privateKey: String = context.privateKey
        val publicKey: String = context.publicKey
        require(!StringUtils.isAnyEmpty(privateKey, publicKey)) { "RSA公私钥均需要提供，公钥加密，私钥解密。" }
    }

    /**
     * 获得当前算法
     */
    override fun algorithm(): AlgorithmType {
        return AlgorithmType.RSA
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    override fun encrypt(value: String?, encodeType: EncodeType): String {
        return if (encodeType === EncodeType.HEX) {
            EncryptUtils.encryptByRsaHex(value, context.publicKey)
        } else {
            EncryptUtils.encryptByRsa(value, context.publicKey)
        }
    }

    /**
     * 解密
     *
     * @param value      待加密字符串
     */
    override fun decrypt(value: String?): String? {
        return EncryptUtils.decryptByRsa(value, context.privateKey)
    }
}
