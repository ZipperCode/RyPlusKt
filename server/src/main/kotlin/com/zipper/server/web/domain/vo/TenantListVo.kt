package com.zipper.server.web.domain.vo

import com.zipper.modules.system.domain.vo.SysTenantVo
import io.github.linpeilie.annotations.AutoMapper

/**
 * 租户列表
 *
 * @author Lion Li
 */
@AutoMapper(target = SysTenantVo::class)
class TenantListVo {
    var tenantId: String? = null

    var companyName: String? = null

    var domain: String? = null
}
