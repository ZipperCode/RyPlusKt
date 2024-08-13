package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import lombok.Data
import java.io.Serial
import java.io.Serializable
import java.util.*

/**
 * 操作日志记录表 oper_log
 * opera
 *
 * @author Lion Li
 */
@Data
@TableName("sys_oper_log")
class SysOperLogEntity : Serializable {
    /**
     * 日志主键
     */
    @field:TableId(value = "oper_id")
    var operId: Long? = null

    /**
     * 租户编号
     */
    var tenantId: String? = null

    /**
     * 操作模块
     */
    var title: String? = null

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    var businessType: Int? = null

    /**
     * 请求方法
     */
    var method: String? = null

    /**
     * 请求方式
     */
    var requestMethod: String? = null

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    var operatorType: Int? = null

    /**
     * 操作人员
     */
    var operName: String? = null

    /**
     * 部门名称
     */
    var deptName: String? = null

    /**
     * 请求url
     */
    var operUrl: String? = null

    /**
     * 操作地址
     */
    var operIp: String? = null

    /**
     * 操作地点
     */
    var operLocation: String? = null

    /**
     * 请求参数
     */
    var operParam: String? = null

    /**
     * 返回参数
     */
    var jsonResult: String? = null

    /**
     * 操作状态（0正常 1异常）
     */
    var status: Int = 0

    /**
     * 错误消息
     */
    var errorMsg: String? = null

    /**
     * 操作时间
     */
    var operTime: Date? = null

    /**
     * 消耗时间
     */
    var costTime: Long? = null

    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
