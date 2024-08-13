package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.mybatis.core.domain.BaseEntity
import java.io.Serial

/**
 * 授权管理对象 sys_client
 *
 */
@TableName("sys_client")
class SysClientEntity : BaseEntity() {
    /**
     * id
     */
    @field:TableId(value = "id")
    var id: Long? = null

    /**
     * 客户端id
     */
    var clientId: String? = null

    /**
     * 客户端key
     */
    var clientKey: String? = null

    /**
     * 客户端秘钥
     */
    var clientSecret: String? = null

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
