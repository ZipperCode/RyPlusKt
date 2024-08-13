package com.zipper.modules.system.domain.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import com.zipper.framework.tanent.core.TenantEntity
import lombok.Data
import lombok.EqualsAndHashCode
import java.io.Serial

/**
 * 部门表 sys_dept
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
class SysDeptEntity : TenantEntity() {
    /**
     * 部门ID
     */
    @field:TableId(value = "dept_id")
    var deptId: Long? = null

    /**
     * 父部门ID
     */
    var parentId: Long? = null

    /**
     * 部门名称
     */
    var deptName: String? = null

    /**
     * 显示顺序
     */
    var orderNum: Int? = null

    /**
     * 负责人
     */
    var leader: Long? = null

    /**
     * 联系电话
     */
    var phone: String? = null

    /**
     * 邮箱
     */
    var email: String? = null

    /**
     * 部门状态:0正常,1停用
     */
    var status: String? = null

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @field:TableLogic
    var delFlag: String? = null

    /**
     * 祖级列表
     */
    var ancestors: String = ""

    companion object {
        @Serial
        var serialVersionUID = 1L
    }
}
