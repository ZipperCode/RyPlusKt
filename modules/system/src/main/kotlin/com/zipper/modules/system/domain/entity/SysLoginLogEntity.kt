package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import lombok.Data
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 系统访问记录表 sys_login_log
 */
@Data
@TableName("sys_login_log")
class SysLoginLogEntity : Serializable {
    /**
     * ID
     */
    @field:TableId(value = "info_id")
    var infoId: Long? = null

    /**
     * 租户编号
     */
    var tenantId: String? = null

    /**
     * 用户账号
     */
    var userName: String? = null

    /**
     * 客户端
     */
    var clientKey: String? = null

    /**
     * 设备类型
     */
    var deviceType: String? = null

    /**
     * 登录状态 0成功 1失败
     */
    var status: String? = null

    /**
     * 登录IP地址
     */
    var ipaddr: String? = null

    /**
     * 登录地点
     */
    var loginLocation: String? = null

    /**
     * 浏览器类型
     */
    var browser: String? = null

    /**
     * 操作系统
     */
    var os: String? = null

    /**
     * 提示消息
     */
    var msg: String? = null

    /**
     * 访问时间
     */
    var loginTime: Date? = null

    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
