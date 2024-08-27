package com.zipper.modules.tenant.domain.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.mybatis.core.domain.BaseMixinEntity
import com.zipper.modules.tenant.domain.SysTenantMixin
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
class SysTenantEntity : BaseMixinEntity(), SysTenantMixin {
    /**
     * id
     */
    @field:TableId(value = "id", type = IdType.AUTO)
    override var id: Long? = null

    /**
     * 租户编号
     */
    override var tenantId: String? = null

    /**
     * 联系人
     */
    override var contactUserName: String? = null

    /**
     * 联系电话
     */
    override var contactPhone: String? = null

    /**
     * 企业名称
     */
    override var companyName: String? = null

    /**
     * 统一社会信用代码
     */
    override var licenseNumber: String? = null

    /**
     * 地址
     */
    override var address: String? = null

    /**
     * 域名
     */
    override var domain: String? = null

    /**
     * 企业简介
     */
    override var intro: String? = null

    /**
     * 备注
     */
    override var remark: String? = null

    /**
     * 租户套餐编号
     */
    override var packageId: Long? = null

    /**
     * 过期时间
     */
    override var expireTime: Date? = null

    /**
     * 用户数量（-1不限制）
     */
    override var accountCount: Long? = null

    /**
     * 租户状态（0正常 1停用）
     */
    override var status: String? = null

    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
