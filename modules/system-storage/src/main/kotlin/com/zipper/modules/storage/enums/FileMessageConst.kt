package com.zipper.modules.storage.enums

import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.MessageUtils
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object FileMessageConst {

    class MessageThrowDelegate(
        private val messageCode: String,
        private vararg val args: Any?
    ) : ReadOnlyProperty<Any, Exception> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Exception {
            return ServiceException(MessageUtils.message(messageCode, *args))
        }
    }

    /**
     * 文件配置未找到
     */

    val ConfigNotFoundError: Exception by MessageThrowDelegate("store.file.config.not.found")

    /**
     * 主配置未找到
     */
    val MasterConfigNotFoundError: Exception by MessageThrowDelegate("store.file.config.master.not.found")

    /**
     * 主配置无法删除
     */
    val UnableDeleteMasterConfigError: Exception by MessageThrowDelegate("store.file.config.master.unable.delete")

    /**
     * 客户端未找到
     */
    val ClientNotFoundError: Exception by MessageThrowDelegate("store.file.client.not.found")
}