package com.zipper.modules.storage.client

import cn.hutool.core.lang.loader.AtomicLoader
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class FileClientMasterDelegate(
    private val loader: (Long) -> FileClient
) {

    private val masterConfigId = AtomicLong(Long.MIN_VALUE)

    private val masterFileClient: AtomicReference<FileClient> = AtomicReference()
    fun getValue(): FileClient {
        synchronized(this) {
            var result = masterFileClient.get()
            if (result == null) {
                result = loader.invoke(masterConfigId.get())
                masterConfigId.set(result.getId())
                if (!masterFileClient.compareAndSet(null, result)) {
                    result = masterFileClient.get()
                }
            }
            return result
        }
    }

    fun setValue(value: Long) {
        synchronized(this) {
            val originConfigId = masterConfigId.get()
            if (originConfigId == value) {
                return
            }
            masterFileClient.set(null)
        }
    }

    /**
     * 如果是主配置，则销毁主配置
     */
    fun checkMasterRelease(value: Long) {
        synchronized(this) {
            if (value == masterConfigId.get()) {
                masterFileClient.set(null)
            }
        }
    }
}