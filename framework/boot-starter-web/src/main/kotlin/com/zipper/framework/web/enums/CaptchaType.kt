package com.zipper.framework.web.enums

import cn.hutool.captcha.generator.CodeGenerator
import cn.hutool.captcha.generator.RandomGenerator
import com.zipper.framework.web.utils.UnsignedMathGenerator

/**
 * 验证码类型
 *
 * @author Lion Li
 */
enum class CaptchaType (
    val clazz: Class<out CodeGenerator>
){
    /**
     * 数字
     */
    MATH(UnsignedMathGenerator::class.java),

    /**
     * 字符
     */
    CHAR(RandomGenerator::class.java);

}
