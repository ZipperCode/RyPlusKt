package com.zipper.framework.encrypt.annotation

import com.zipper.framework.encrypt.enums.AlgorithmType
import com.zipper.framework.encrypt.enums.EncodeType
import java.lang.annotation.Inherited


/**
 * 字段加密注解
 *
 * @author 老马
 */
@MustBeDocumented
@Inherited
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class EncryptField(
    /**
     * 加密算法
     */
    val algorithm: AlgorithmType = AlgorithmType.DEFAULT,
    /**
     * 秘钥。AES、SM4需要
     */
    val password: String = "",
    /**
     * 公钥。RSA、SM2需要
     */
    val publicKey: String = "",
    /**
     * 私钥。RSA、SM2需要
     */
    val privateKey: String = "",
    /**
     * 编码方式。对加密算法为BASE64的不起作用
     */
    val encode: EncodeType = EncodeType.DEFAULT
)
