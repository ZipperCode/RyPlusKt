package com.zipper.modules.auth.domain.param

import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import com.zipper.framework.mybatis.core.domain.BaseMixinVo
import com.zipper.modules.auth.domain.SysClientMixin
import com.zipper.modules.auth.domain.entity.SysClientEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@AutoMapper(target = SysClientEntity::class, reverseConvertGenerate = false)
class SysClientSaveParam : BaseMixinVo(), SysClientMixin {

    /**
     * id
     */
    @field:NotNull(message = "id不能为空", groups = [EditGroup::class])
    override var id: Long? = null

    /**
     * 客户端id
     */
    override var clientId: String? = null

    /**
     * 客户端key
     */
    @field:NotBlank(message = "客户端key不能为空", groups = [AddGroup::class, EditGroup::class])
    override var clientKey: String? = ""

    /**
     * 客户端秘钥
     */
    @field:NotBlank(message = "客户端秘钥不能为空", groups = [AddGroup::class, EditGroup::class])
    override var clientSecret: String? = ""

    /**
     * 授权类型
     */
    @field:NotNull(message = "授权类型不能为空", groups = [AddGroup::class, EditGroup::class])
    var grantTypeList: MutableList<String>? = null

    /**
     * 授权类型
     */
    override var grantType: String? = null

    /**
     * 设备类型
     */
    override var deviceType: String? = null

    /**
     * token活跃超时时间
     */
    override var activeTimeout: Long? = null

    /**
     * token固定超时时间
     */
    override var timeout: Long? = null

    /**
     * 状态（0正常 1停用）
     */
    override var status: String? = null
}