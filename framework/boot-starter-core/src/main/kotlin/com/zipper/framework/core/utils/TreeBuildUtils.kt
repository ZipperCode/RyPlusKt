package com.zipper.framework.core.utils

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.lang.tree.Tree
import cn.hutool.core.lang.tree.TreeNodeConfig
import cn.hutool.core.lang.tree.TreeUtil
import cn.hutool.core.lang.tree.parser.NodeParser
import lombok.AccessLevel
import lombok.NoArgsConstructor
import com.zipper.framework.core.utils.reflect.ReflectUtils

/**
 * 扩展 hutool TreeUtil 封装系统树构建
 *
 * @author Lion Li
 */
object TreeBuildUtils : TreeUtil() {
    /**
     * 根据前端定制差异化字段
     */
    val DEFAULT_CONFIG: TreeNodeConfig = TreeNodeConfig.DEFAULT_CONFIG.setNameKey("label")

    inline fun <T, reified K> build(list: List<T>, nodeParser: NodeParser<T, K>): List<Tree<K>> {
        if (CollUtil.isEmpty(list)) {
            return emptyList()
        }
        val k = ReflectUtils.invokeGetter<K>(list[0] as Any, "parentId")
        return build(list, k, DEFAULT_CONFIG, nodeParser)
    }
}
