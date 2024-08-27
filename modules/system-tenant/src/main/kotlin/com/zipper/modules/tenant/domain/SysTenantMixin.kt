package com.zipper.modules.tenant.domain

import com.zipper.framework.mybatis.core.domain.CreatorMixin
import com.zipper.framework.mybatis.core.domain.LogicDeleteMixin
import com.zipper.framework.mybatis.core.domain.UpdaterMixin
import java.io.Serializable
import java.util.*

interface SysTenantMixin : CreatorMixin, UpdaterMixin, Serializable {

    var id: Long?

    /**
     * 租户编号
     */
    var tenantId: String?

    /**
     * 联系人
     */
    var contactUserName: String?

    /**
     * 联系电话
     */
    var contactPhone: String?

    /**
     * 企业名称
     */
    var companyName: String?

    /**
     * 统一社会信用代码
     */
    var licenseNumber: String?

    /**
     * 地址
     */
    var address: String?

    /**
     * 域名
     */
    var domain: String?

    /**
     * 企业简介
     */
    var intro: String?

    /**
     * 备注
     */
    var remark: String?

    /**
     * 租户套餐编号
     */
    var packageId: Long?

    /**
     * 过期时间
     */
    var expireTime: Date?

    /**
     * 用户数量（-1不限制）
     */
    var accountCount: Long?

    /**
     * 租户状态（0正常 1停用）
     */
    var status: String?
}