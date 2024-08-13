package com.zipper.modules.system.service.log

import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysLoginLogBo
import com.zipper.modules.system.domain.vo.SysLoginLogVo

/**
 * 系统访问日志情况信息 服务层
 *
 * @author Lion Li
 */
interface ISysLoginLogService {
    fun selectPageLoginLogList(loginLog: SysLoginLogBo, pageQuery: PageQuery): TableDataInfo<SysLoginLogVo>

    /**
     * 新增系统登录日志
     *
     * @param bo 访问日志对象
     */
    fun insertLoginLog(bo: SysLoginLogBo)

    /**
     * 查询系统登录日志集合
     *
     * @param loginLogBo 访问日志对象
     * @return 登录记录集合
     */
    fun selectLoginLogList(loginLogBo: SysLoginLogBo): List<SysLoginLogVo>

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    fun deleteLoginLogByIds(infoIds: Array<Long>): Int

    /**
     * 清空系统登录日志
     */
    fun cleanLoginLog()
}
