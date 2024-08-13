package com.zipper.framework.core.utils

import cn.hutool.extra.spring.SpringUtil
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator

/**
 * Validator 校验框架工具
 *
 * @author Lion Li
 */
object ValidatorUtils {
    private val VALID: Validator by lazy {
        SpringUtil.getBean<Validator>(Validator::class.java)
    }

    @JvmStatic
    fun <T> validate(target: T, vararg groups: Class<*>?) {
        val validate = VALID.validate(target, *groups)
        if (validate.isNotEmpty()) {
            throw ConstraintViolationException("参数校验异常", validate)
        }
    }
}
