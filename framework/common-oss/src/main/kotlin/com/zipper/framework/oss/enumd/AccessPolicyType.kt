package com.zipper.framework.oss.enumd

import com.amazonaws.services.s3.model.CannedAccessControlList

/**
 * 桶访问策略配置
 *
 */
enum class AccessPolicyType(
    /**
     * 桶 权限类型
     */
    val type: String,

    /**
     * 文件对象 权限类型
     */
    val acl: CannedAccessControlList,

    /**
     * 桶策略类型
     */
    val policyType: PolicyType
) {
    /**
     * private
     */
    PRIVATE("0", CannedAccessControlList.Private, PolicyType.WRITE),

    /**
     * public
     */
    PUBLIC("1", CannedAccessControlList.PublicRead, PolicyType.READ),

    /**
     * custom
     */
    CUSTOM("2", CannedAccessControlList.PublicRead, PolicyType.READ);


    companion object {
        fun getByType(type: String): AccessPolicyType {
            return entries.firstOrNull{it.type == type} ?: throw RuntimeException("'type' not found By $type")
        }
    }
}
