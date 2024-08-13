package com.zipper.framework.core.utils.sql

import org.apache.commons.lang3.StringUtils

/**
 * sql操作工具类
 *
 * @author ruoyi
 */
object SqlUtil {
    /**
     * 定义常用的 sql关键字
     */
    const val SQL_REGEX: String = "select |insert |delete |update |drop |count |exec |chr |mid |master |truncate |char |and |declare "

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    const val SQL_PATTERN: String = "[a-zA-Z0-9_\\ \\,\\.]+"

    /**
     * 检查字符，防止注入绕过
     */
    @JvmStatic
    fun escapeOrderBySql(value: String): String {
        require(!(value.isNotEmpty() && !isValidOrderBySql(value))) { "参数不符合规范，不能进行查询" }
        return value
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    @JvmStatic
    fun isValidOrderBySql(value: String): Boolean {
        return value.matches(SQL_PATTERN.toRegex())
    }

    /**
     * SQL关键字检查
     */
    @JvmStatic
    fun filterKeyword(value: String?) {
        if (StringUtils.isEmpty(value)) {
            return
        }
        val sqlKeywords: Array<String> = StringUtils.split(SQL_REGEX, "\\|")
        for (sqlKeyword in sqlKeywords) {
            require(StringUtils.indexOfIgnoreCase(value, sqlKeyword) <= -1) { "参数存在SQL注入风险" }
        }
    }
}
