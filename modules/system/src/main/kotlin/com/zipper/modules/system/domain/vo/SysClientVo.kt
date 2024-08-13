package com.zipper.modules.system.domain.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.zipper.modules.system.domain.entity.SysClientEntity
import io.github.linpeilie.annotations.AutoMapper
import lombok.Data
import org.zipper.framework.excel.annotation.ExcelDictFormat
import org.zipper.framework.excel.convert.ExcelDictConvert
import java.io.Serial
import java.io.Serializable


/**
 * 授权管理视图对象 sys_client
 *
 * @author Michelle.Chung
 * @date 2023-05-15
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysClientEntity::class)
class SysClientVo : Serializable {
    /**
     * id
     */
    @field:ExcelProperty(value = ["id"])
    var id: Long? = null

    /**
     * 客户端id
     */
    @field:ExcelProperty(value = ["客户端id"])
    var clientId: String? = null

    /**
     * 客户端key
     */
    @field:ExcelProperty(value = ["客户端key"])
    var clientKey: String? = null

    /**
     * 客户端秘钥
     */
    @field:ExcelProperty(value = ["客户端秘钥"])
    var clientSecret: String? = null

    /**
     * 授权类型
     */
    @field:ExcelProperty(value = ["授权类型"])
    var grantTypeList: List<String>? = null

    /**
     * 授权类型
     */
    var grantType: String = ""

    /**
     * 设备类型
     */
    var deviceType: String? = null

    /**
     * token活跃超时时间
     */
    @field:ExcelProperty(value = ["token活跃超时时间"])
    var activeTimeout: Long? = null

    /**
     * token固定超时时间
     */
    @field:ExcelProperty(value = ["token固定超时时间"])
    var timeout: Long? = null

    /**
     * 状态（0正常 1停用）
     */
    @field:ExcelProperty(value = ["状态"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(readConverterExp = "0=正常,1=停用")
    var status: String? = null


    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
