package com.zipper.framework.tanent.handle

import cn.hutool.core.collection.ListUtil
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler
import com.zipper.framework.tanent.config.properties.TenantProperties
import com.zipper.framework.tanent.helper.TenantHelper
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.expression.NullValue
import net.sf.jsqlparser.expression.StringValue
import org.apache.commons.lang3.StringUtils
import com.zipper.framework.core.ext.log

/**
 * 自定义租户处理器
 *
 * @author Lion Li
 */
class PlusTenantLineHandler(
    private val tenantProperties: TenantProperties
) : TenantLineHandler {


    override fun getTenantId(): Expression {
        val tenantId = TenantHelper.tenantId
        if (StringUtils.isBlank(tenantId)) {
            log.error("无法获取有效的租户id -> Null")
            return NullValue()
        }
        // 返回固定租户
        return StringValue(tenantId)
    }

    override fun ignoreTable(tableName: String): Boolean {
        val tenantId: String? = TenantHelper.tenantId
        // 判断是否有租户
        if (StringUtils.isNotBlank(tenantId)) {
            // 不需要过滤租户的表
            val excludes = tenantProperties.excludes
            // 非业务表
            val tables: MutableList<String> = ListUtil.toList(
                "gen_table",
                "gen_table_column"
            )
            tables.addAll(excludes)
            return tables.contains(tableName)
        }
        return true
    }
}
