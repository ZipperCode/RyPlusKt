package com.zipper.framework.encrypt.interceptor

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import lombok.AllArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.ibatis.executor.resultset.ResultSetHandler
import org.apache.ibatis.plugin.*
import com.zipper.framework.core.ext.log
import com.zipper.framework.encrypt.annotation.EncryptField
import com.zipper.framework.encrypt.core.EncryptContext
import com.zipper.framework.encrypt.core.EncryptorManager
import com.zipper.framework.encrypt.enums.AlgorithmType
import com.zipper.framework.encrypt.enums.EncodeType
import com.zipper.framework.encrypt.config.properties.EncryptorProperties
import java.lang.reflect.Field
import java.sql.Statement
import java.util.*

/**
 * 出参解密拦截器
 *
 * @author 老马
 * @version 4.6.0
 */
@Intercepts(
    Signature(
        type = ResultSetHandler::class,
        method = "handleResultSets",
        args = [Statement::class]
    )
)
class MybatisDecryptInterceptor(
    private val encryptorManager: EncryptorManager,
    private val defaultProperties: com.zipper.framework.encrypt.config.properties.EncryptorProperties
) : Interceptor {

    override fun intercept(invocation: Invocation): Any? {
        // 获取执行mysql执行结果
        val result = invocation.proceed() ?: return null
        decryptHandler(result)
        return result
    }

    /**
     * 解密对象
     *
     * @param sourceObject 待加密对象
     */
    private fun decryptHandler(sourceObject: Any?) {
        sourceObject ?: return
        if (sourceObject is Map<*, *>) {
            HashSet(sourceObject.values).forEach(this::decryptHandler)
            return
        }
        if (sourceObject is List<*>) {
            // 判断第一个元素是否含有注解。如果没有直接返回，提高效率
            val firstItem = sourceObject.firstOrNull() ?:return

            if (CollUtil.isEmpty(encryptorManager.getFieldCache(firstItem.javaClass))) {
                return
            }
            sourceObject.forEach(this::decryptHandler)
            return
        }
        val fields = encryptorManager.getFieldCache(sourceObject.javaClass)
        try {
            for (field in fields) {
                field[sourceObject] = decryptField(Convert.toStr(field[sourceObject]), field)
            }
        } catch (e: Exception) {
            log.error("处理解密字段时出错", e)
        }
    }

    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    private fun decryptField(value: String, field: Field): String? {
        if (ObjectUtil.isNull(value)) {
            return null
        }
        val encryptField = field.getAnnotation(com.zipper.framework.encrypt.annotation.EncryptField::class.java)
        val encryptContext = com.zipper.framework.encrypt.core.EncryptContext(encryptField, defaultProperties)
        return encryptorManager.decrypt(value, encryptContext)
    }

    override fun plugin(target: Any): Any {
        return Plugin.wrap(target, this)
    }

    override fun setProperties(properties: Properties) {
    }
}
