package com.zipper.modules.system.service.datascope

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.convert.Convert
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.mybatis.helper.DataBaseHelper
import com.zipper.modules.system.domain.entity.SysDeptEntity
import com.zipper.modules.system.domain.entity.SysRoleDeptEntity
import com.zipper.modules.system.mapper.SysDeptMapper
import com.zipper.modules.system.mapper.SysRoleDeptMapper
import org.springframework.stereotype.Service
import com.zipper.framework.core.utils.StreamUtils.join
import com.zipper.framework.core.utils.StreamUtils.toList

/**
 * 数据权限 实现
 *
 *
 * 注意: 此Service内不允许调用标注`数据权限`注解的方法
 * 例如: deptMapper.selectList 此 selectList 方法标注了`数据权限`注解 会出现循环解析的问题
 *
 * @author Lion Li
 */
@Service("sdss")
class SysDataScopeServiceImpl(
    private val roleDeptMapper: SysRoleDeptMapper,
    private val deptMapper: SysDeptMapper
) : ISysDataScopeService {
    override fun getRoleCustom(roleId: Long?): String? {
        val list = roleDeptMapper.selectList(
            KtQueryWrapper(SysRoleDeptEntity::class.java)
                .select(SysRoleDeptEntity::deptId)
                .eq(SysRoleDeptEntity::roleId, roleId)
        )

        if (CollUtil.isNotEmpty(list)) {
            return join(list) { Convert.toStr(it.deptId) }
        }
        return null
    }

    override fun getDeptAndChild(deptId: Long?): String? {
        val deptList = deptMapper.selectList(
            MybatisKt.ktQuery<SysDeptEntity>()
                .select(SysDeptEntity::deptId)
                .apply(DataBaseHelper.findInSet(deptId, "ancestors"))
        )

        val ids = deptList.map { it.deptId } + deptId
        if (CollUtil.isNotEmpty(ids)) {
            return join(ids) { Convert.toStr(it) }
        }
        return null
    }
}
