package com.zipper.modules.system.domain.vo

import cn.hutool.core.lang.tree.Tree
import lombok.Data

/**
 * 角色部门列表树信息
 *
 * @author Michelle.Chung
 */
@Data
class DeptTreeSelectVo {
    /**
     * 选中部门列表
     */
    var  checkedKeys: List<Long>? = null

    /**
     * 下拉树结构列表
     */
    var  depts: List<Tree<Long>>? = null
}
