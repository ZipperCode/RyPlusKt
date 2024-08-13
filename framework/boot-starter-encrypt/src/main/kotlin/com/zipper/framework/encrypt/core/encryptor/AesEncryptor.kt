package com.zipper.framework.encrypt.core.encryptor

import com.zipper.framework.encrypt.core.EncryptContext
import com.zipper.framework.encrypt.enums.AlgorithmType
import com.zipper.framework.encrypt.enums.EncodeType
import com.zipper.framework.encrypt.utils.EncryptUtils


/**
 * AES算法实现
 *
 * @author 老马
 * @version 4.6.0
 */
class AesEncryptor(context: com.zipper.framework.encrypt.core.EncryptContext) : AbstractEncryptor(context) {


    /**
     * 获得当前算法
     */
    override fun algorithm(): AlgorithmType {
        return AlgorithmType.AES
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    override fun encrypt(value: String?, encodeType: EncodeType): String {
        return if (encodeType === EncodeType.HEX) {
            EncryptUtils.encryptByAesHex(value, context.password)
        } else {
            EncryptUtils.encryptByAes(value, context.password)
        }
    }

    /**
     * 解密
     *
     * @param value      待加密字符串
     */
    override fun decrypt(value: String?): String? {
        return EncryptUtils.decryptByAes(value, context.password)
    }
}
