package com.zipper.modules.tenant.domain.param

import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import com.zipper.framework.mybatis.core.domain.BaseMixinVo
import com.zipper.modules.tenant.domain.SysTenantMixin
import com.zipper.modules.tenant.domain.entity.SysTenantEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.Data
import lombok.EqualsAndHashCode
import java.util.*

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysTenantEntity::class, reverseConvertGenerate = false)
class SysTenantSaveParam : BaseMixinVo(), SysTenantMixin {
    /**
     * id
     */
    @field:NotNull(message = "id不能为空", groups = [EditGroup::class])
    override var id: Long? = null

    /**
     * 租户编号
     */
    override var tenantId: String? = null

    /**
     * 联系人
     */
    @field:NotBlank(message = "联系人不能为空", groups = [AddGroup::class, EditGroup::class])
    override var contactUserName: String? = null

    /**
     * 联系电话
     */
    @field:NotBlank(message = "联系电话不能为空", groups = [AddGroup::class, EditGroup::class])
    override var contactPhone: String? = null

    /**
     * 企业名称
     */
    @field:NotBlank(message = "企业名称不能为空", groups = [AddGroup::class, EditGroup::class])
    override var companyName: String? = null

    val requireCompanyName: String get() = companyName!!

    /**
     * 用户名（创建系统用户）
     */
    @field:NotBlank(message = "用户名不能为空", groups = [AddGroup::class])
    var username: String? = null

    val requireUsername get() = username!!

    /**
     * 密码（创建系统用户）
     */
    @field:NotBlank(message = "密码不能为空", groups = [AddGroup::class])
    var password: String? = null

    val requirePassword get() = password!!

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
    @field:NotNull(message = "租户套餐不能为空", groups = [AddGroup::class])
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
}