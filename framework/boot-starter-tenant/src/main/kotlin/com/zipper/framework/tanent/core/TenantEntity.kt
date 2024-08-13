package com.zipper.framework.tanent.core

import com.zipper.framework.mybatis.core.domain.BaseEntity

/**
 * 租户基类
 *
 * @author Michelle.Chung
 */
abstract class TenantEntity : BaseEntity() {
    /**
     * 租户编号
     */
    var tenantId: String? = null
}
