package com.zipper.modules.system.domain.bo

import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.modules.system.domain.entity.SysClientEntity
import io.github.linpeilie.annotations.AutoMapper
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup

/**
 * 授权管理业务对象 sys_client
 *
 * @author Michelle.Chung
 * @date 2023-05-15
 */
@AutoMapper(target = SysClientEntity::class, reverseConvertGenerate = false)
class SysClientBo : BaseEntity() {
    /**
     * id
     */
    @field:NotNull(message = "id不能为空", groups = [EditGroup::class])
    var id: Long? = null

    /**
     * 客户端id
     */
    var clientId: String? = null

    /**
     * 客户端key
     */
    @field:NotBlank(message = "客户端key不能为空", groups = [AddGroup::class, EditGroup::class])
    var clientKey: String = ""

    /**
     * 客户端秘钥
     */
    @field:NotBlank(message = "客户端秘钥不能为空", groups = [AddGroup::class, EditGroup::class])
    var clientSecret: String = ""

    /**
     * 授权类型
     */
    @field:NotNull(message = "授权类型不能为空", groups = [AddGroup::class, EditGroup::class])
    var grantTypeList: MutableList<String>? = null

    /**
     * 授权类型
     */
    var grantType: String? = null

    /**
     * 设备类型
     */
    var deviceType: String? = null

    /**
     * token活跃超时时间
     */
    var activeTimeout: Long? = null

    /**
     * token固定超时时间
     */
    var timeout: Long? = null

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null
}
