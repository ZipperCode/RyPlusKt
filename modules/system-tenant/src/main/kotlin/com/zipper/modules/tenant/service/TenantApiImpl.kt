package com.zipper.modules.tenant.service

import cn.hutool.core.convert.Convert
import com.zipper.framework.core.modules.ITenantApi
import com.zipper.framework.core.utils.StringUtilsExt
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.tanent.core.TenantEntity
import com.zipper.modules.tenant.domain.entity.SysTenantEntity
import com.zipper.modules.tenant.mapper.SysTenantMapper
import com.zipper.modules.tenant.mapper.SysTenantPackageMapper
import org.springframework.stereotype.Service

@Service(ITenantApi.IMPL)
class TenantApiImpl(
    private val tenantMapper: SysTenantMapper,
    private val tenantPackageMapper: SysTenantPackageMapper,
) : ITenantApi {

    override fun getPackageMenuIds(packageId: Long): Pair<Boolean, List<Long>> {
        val tenantPackage = tenantPackageMapper.selectById(packageId)
        return Pair(tenantPackage.menuCheckStrictly!!, StringUtilsExt.splitTo(tenantPackage.menuIds, Convert::toLong))
    }

    override fun checkAccountBalance(tenantId: String, invokeUserCount: () -> Long): Boolean {
        val tenant = tenantMapper.selectOne(
            MybatisKt.ktQuery<SysTenantEntity>().eq(SysTenantEntity::tenantId, tenantId)
        )
        // 如果余额为-1代表不限制
        if (tenant?.accountCount == -1L) {
            return true
        }
        val userNumber = invokeUserCount()
        // 如果余额大于0代表还有可用名额
        return (tenant?.accountCount ?: 0) - userNumber > 0
    }
}