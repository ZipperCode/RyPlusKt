package com.zipper.framework.web.core

import com.zipper.framework.core.domain.R
import com.zipper.framework.core.utils.StringUtilsExt.format


/**
 * web层通用数据处理
 *
 * @author Lion Li
 */
abstract class BaseController {

    protected fun success(): R<Void> {
        return R.ok()
    }

    protected fun <T> success(data: T): R<T> {
        return R.ok(data)
    }

    protected fun <T> successOrNull(data: T): R<T?> {
        return R.ok(data)
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected fun toAjax(rows: Number): R<Void> {
        return if (rows.toInt() > 0) R.ok() else R.fail()
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected fun toAjax(result: Boolean): R<Void> {
        return if (result) R.ok() else R.fail()
    }

    /**
     * 页面跳转
     */
    fun redirect(url: String?): String {
        return format("redirect:{}", url)
    }
}
