package com.zipper.modules.system.service.log

import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysOperLogBo
import com.zipper.modules.system.domain.vo.SysOperLogVo

/**
 * 操作日志 服务层
 *
 * @author Lion Li
 */
interface ISysOperLogService {
    fun selectPageOperLogList(operLog: SysOperLogBo, pageQuery: PageQuery): TableDataInfo<SysOperLogVo>

    /**
     * 新增操作日志
     *
     * @param bo 操作日志对象
     */
    fun insertOperlog(bo: SysOperLogBo)

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    fun selectOperLogList(operLog: SysOperLogBo): List<SysOperLogVo>

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    fun deleteOperLogByIds(operIds: Array<Long>): Int

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    fun selectOperLogById(operId: Long?): SysOperLogVo?

    /**
     * 清空操作日志
     */
    fun cleanOperLog()
}
