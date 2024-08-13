package com.zipper.modules.system.service.client

import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysClientBo
import com.zipper.modules.system.domain.entity.SysClientEntity
import com.zipper.modules.system.domain.vo.SysClientVo

/**
 * 客户端管理Service接口
 *
 * @author Michelle.Chung
 * @date 2023-06-18
 */
interface ISysClientService {
    /**
     * 查询客户端管理
     */
    fun queryById(id: Long?): SysClientVo?

    /**
     * 查询客户端信息基于客户端id
     */
    fun queryByClientId(clientId: String?): SysClientEntity?

    /**
     * 查询客户端管理列表
     */
    fun queryPageList(bo: SysClientBo, pageQuery: PageQuery): TableDataInfo<SysClientVo>

    /**
     * 查询客户端管理列表
     */
    fun queryList(bo: SysClientBo): List<SysClientVo>

    /**
     * 新增客户端管理
     */
    fun insertByBo(bo: SysClientBo): Boolean

    /**
     * 修改客户端管理
     */
    fun updateByBo(bo: SysClientBo): Boolean

    /**
     * 修改状态
     */
    fun updateUserStatus(id: Long, status: String): Int

    /**
     * 校验并批量删除客户端管理信息
     */
    fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean
}
