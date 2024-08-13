package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.mybatis.core.domain.BaseEntity
import lombok.Data
import lombok.EqualsAndHashCode
import java.io.Serial
import java.util.*

/**
 * 租户对象 sys_tenant
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
class SysTenantEntity : BaseEntity() {
    /**
     * id
     */
    @field:TableId(value = "id")
    var id: Long? = null

    /**
     * 租户编号
     */
    var tenantId: String? = null

    /**
     * 联系人
     */
    var contactUserName: String? = null

    /**
     * 联系电话
     */
    var contactPhone: String? = null

    /**
     * 企业名称
     */
    var companyName: String? = null

    /**
     * 统一社会信用代码
     */
    var licenseNumber: String? = null

    /**
     * 地址
     */
    var address: String? = null

    /**
     * 域名
     */
    var domain: String? = null

    /**
     * 企业简介
     */
    var intro: String? = null

    /**
     * 备注
     */
    var remark: String? = null

    /**
     * 租户套餐编号
     */
    var packageId: Long? = null

    /**
     * 过期时间
     */
    var expireTime: Date? = null

    /**
     * 用户数量（-1不限制）
     */
    var accountCount: Long? = null

    /**
     * 租户状态（0正常 1停用）
     */
    var status: String? = null

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @field:TableLogic
    var delFlag: String? = null

    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
