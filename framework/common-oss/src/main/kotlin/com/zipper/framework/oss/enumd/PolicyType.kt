package com.zipper.framework.oss.enumd

/**
 * minio策略配置
 *
 * @param type 类型
 */
enum class PolicyType(
    val type: String
) {
    /**
     * 只读
     */
    READ("read-only"),

    /**
     * 只写
     */
    WRITE("write-only"),

    /**
     * 读写
     */
    READ_WRITE("read-write");
}
