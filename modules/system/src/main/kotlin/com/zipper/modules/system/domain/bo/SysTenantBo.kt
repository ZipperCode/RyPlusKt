package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysTenantEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.Data
import lombok.EqualsAndHashCode
import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import java.util.*

/**
 * 租户业务对象 sys_tenant
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysTenantEntity::class, reverseConvertGenerate = false)
class SysTenantBo : BaseEntity() {
    /**
     * id
     */
    @field:NotNull(message = "id不能为空", groups = [EditGroup::class])
    var id: Long? = null

    /**
     * 租户编号
     */
    var tenantId: String? = null

    /**
     * 联系人
     */
    @field:NotBlank(message = "联系人不能为空", groups = [AddGroup::class, EditGroup::class])
    var contactUserName: String? = null

    /**
     * 联系电话
     */
    @field:NotBlank(message = "联系电话不能为空", groups = [AddGroup::class, EditGroup::class])
    var contactPhone: String? = null

    /**
     * 企业名称
     */
    @field:NotBlank(message = "企业名称不能为空", groups = [AddGroup::class, EditGroup::class])
    var companyName: String? = null

    /**
     * 用户名（创建系统用户）
     */
    @field:NotBlank(message = "用户名不能为空", groups = [AddGroup::class])
    var username: String? = null

    /**
     * 密码（创建系统用户）
     */
    @field:NotBlank(message = "密码不能为空", groups = [AddGroup::class])
    var password: String? = null

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
    @field:NotNull(message = "租户套餐不能为空", groups = [AddGroup::class])
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
}
