package com.zipper.framework.mybatis.core.page

data class PageResult<T>(
    val code: Int = 0,
    val msg: String? = null,
) {
}