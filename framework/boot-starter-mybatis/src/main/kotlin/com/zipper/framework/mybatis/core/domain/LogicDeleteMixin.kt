package com.zipper.framework.mybatis.core.domain

interface LogicDeleteMixin : IMixin {

    @set:DeleteStatus
    var deleted: Int

    companion object {
        /**
         * 0 代表存在
         */
        const val NORMAL = 0

        /**
         * 代表删除
         */
        const val DELETED = 2
    }

    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_SETTER)
    annotation class DeleteStatus
}