package com.zipper.modules.tenant.domain.param

import com.zipper.framework.core.annotation.NoArgs
import com.zipper.framework.mybatis.core.page.PageQuery
import lombok.Data

/**
 * 租户查询条件参数
 */
@NoArgs
@Data
data class SysTenantQueryParam(
    val tenantId: String?,
    val contactUserName: String?,
    val contactPhone: String?,
    val companyName: String?,
    val status: String?
)