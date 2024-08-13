package com.zipper.framework.encrypt.core

import cn.hutool.core.util.ReflectUtil
import com.zipper.framework.encrypt.annotation.EncryptField
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

/**
 * 加密管理类
 *
 * @author 老马
 * @version 4.6.0
 */
class EncryptorManager {
    /**
     * 缓存加密器
     */
    private val encryptorMap: MutableMap<com.zipper.framework.encrypt.core.EncryptContext, IEncryptor> = ConcurrentHashMap()

    /**
     * 类加密字段缓存
     */
    private val fieldCache: MutableMap<Class<*>, Set<Field>> = ConcurrentHashMap()

    /**
     * 获取类加密字段缓存
     */
    fun getFieldCache(sourceClazz: Class<*>): Set<Field> {
        return fieldCache.computeIfAbsent(sourceClazz) { clazz: Class<*>? ->
            var tempClazz = clazz
            var fieldSet: MutableSet<Field> = HashSet()
            while (tempClazz != null) {
                val fields = tempClazz.declaredFields
                fieldSet.addAll(listOf(*fields))
                tempClazz = tempClazz.superclass
            }
            fieldSet = fieldSet.filter { it.isAnnotationPresent(com.zipper.framework.encrypt.annotation.EncryptField::class.java) && it.type == String::class.java }.toMutableSet()
            for (field in fieldSet) {
                field.isAccessible = true
            }
            fieldSet
        }
    }

    /**
     * 注册加密执行者到缓存
     *
     * @param encryptContext 加密执行者需要的相关配置参数
     */
    fun registAndGetEncryptor(encryptContext: com.zipper.framework.encrypt.core.EncryptContext): IEncryptor? {
        if (encryptorMap.containsKey(encryptContext)) {
            return encryptorMap[encryptContext]
        }
        val encryptor: IEncryptor = ReflectUtil.newInstance(encryptContext.algorithm.clazz, encryptContext)!!
        encryptorMap[encryptContext] = encryptor
        return encryptor
    }

    /**
     * 移除缓存中的加密执行者
     *
     * @param encryptContext 加密执行者需要的相关配置参数
     */
    fun removeEncryptor(encryptContext: com.zipper.framework.encrypt.core.EncryptContext) {
        encryptorMap.remove(encryptContext)
    }

    /**
     * 根据配置进行加密。会进行本地缓存对应的算法和对应的秘钥信息。
     *
     * @param value          待加密的值
     * @param encryptContext 加密相关的配置信息
     */
    fun encrypt(value: String?, encryptContext: com.zipper.framework.encrypt.core.EncryptContext): String? {
        val encryptor = this.registAndGetEncryptor(encryptContext)
        return encryptor!!.encrypt(value, encryptContext.encode)
    }

    /**
     * 根据配置进行解密
     *
     * @param value          待解密的值
     * @param encryptContext 加密相关的配置信息
     */
    fun decrypt(value: String?, encryptContext: com.zipper.framework.encrypt.core.EncryptContext): String? {
        val encryptor = this.registAndGetEncryptor(encryptContext)
        return encryptor!!.decrypt(value)
    }
}
