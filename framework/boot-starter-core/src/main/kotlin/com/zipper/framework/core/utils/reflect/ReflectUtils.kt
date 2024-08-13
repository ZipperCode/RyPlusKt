package com.zipper.framework.core.utils.reflect

import cn.hutool.core.util.ReflectUtil
import org.apache.commons.lang3.StringUtils
import com.zipper.framework.core.utils.ktext.forceCast
import com.zipper.framework.core.utils.ktext.forceCastOrNull

/**
 * 反射工具类. 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 *
 * @author Lion Li
 */
object ReflectUtils : ReflectUtil() {
    private const val SETTER_PREFIX = "set"

    const val GETTER_PREFIX = "get"

    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    @JvmStatic
     fun <E> invokeGetter(obj: Any, propertyName: String?): E? {
        try {
            var target: Any? = obj
            for (name in StringUtils.split(propertyName, ".")) {
                val getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name)
                target = invoke(target, getterMethodName)
            }
            return target.forceCastOrNull()
        } catch (e:Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     * 支持多级，如：对象名.对象名.方法
     */
    @JvmStatic
    fun <E> invokeSetter(obj: Any, propertyName: String?, value: E) {
        var target = obj
        val names = StringUtils.split(propertyName, ".")
        for (i in names.indices) {
            if (i < names.size - 1) {
                val getterMethodName = GETTER_PREFIX + StringUtils.capitalize(
                    names[i]
                )
               target = invoke(target, getterMethodName)
            } else {
                val setterMethodName = SETTER_PREFIX + StringUtils.capitalize(
                    names[i]
                )
                val method = getMethodByName(target.javaClass, setterMethodName)
                invoke<Any>(target, method, value)
            }
        }
    }
}
