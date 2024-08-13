package com.zipper.modules.system.service.log

import cn.hutool.core.util.ArrayUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.log.event.OperLogEvent
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysOperLogBo
import com.zipper.modules.system.domain.entity.SysOperLogEntity
import com.zipper.modules.system.domain.vo.SysOperLogVo
import com.zipper.modules.system.mapper.SysOperLogMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.framework.core.utils.ip.AddressUtils
import java.util.*

/**
 * 操作日志 服务层处理
 *
 * @author Lion Li
 */
@Service
class SysOperLogServiceImpl(private val baseMapper: SysOperLogMapper) : ISysOperLogService {
    /**
     * 操作日志记录
     *
     * @param operLogEvent 操作日志事件
     */
    @Async
    @EventListener
    fun recordOper(operLogEvent: OperLogEvent) {
        val operLog = convert(operLogEvent, SysOperLogBo::class.java)
        // 远程查询操作地点
        operLog.operLocation = AddressUtils.getRealAddressByIP(operLog.operIp)
        insertOperlog(operLog)
    }

    override fun selectPageOperLogList(operLog: SysOperLogBo, pageQuery: PageQuery): TableDataInfo<SysOperLogVo> {
        val params: Map<String, Any?> = operLog.params
        val lqw = KtQueryWrapper(SysOperLogEntity::class.java)
            .like(StringUtils.isNotBlank(operLog.operIp), SysOperLogEntity::operIp, operLog.operIp)
            .like(StringUtils.isNotBlank(operLog.title), SysOperLogEntity::title, operLog.title)
            .eq(
                operLog.businessType != null && operLog.businessType!! > 0,
                SysOperLogEntity::businessType, operLog.businessType
            )
            .func { f: KtQueryWrapper<SysOperLogEntity> ->
                if (ArrayUtil.isNotEmpty(operLog.businessTypes)) {
                    f.`in`(SysOperLogEntity::businessType, listOf(*operLog.businessTypes))
                }
            }
            .eq(
                operLog.status != null,
                SysOperLogEntity::status, operLog.status
            )
            .like(StringUtils.isNotBlank(operLog.operName), SysOperLogEntity::operName, operLog.operName)
            .between(
                params["beginTime"] != null && params["endTime"] != null,
                SysOperLogEntity::operTime, params["beginTime"], params["endTime"]
            )
        if (StringUtils.isBlank(pageQuery.orderByColumn)) {
            pageQuery.orderByColumn = "oper_id"
            pageQuery.isAsc = "desc"
        }
        val page = baseMapper.selectVoPage<Page<SysOperLogVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(page)
    }

    /**
     * 新增操作日志
     *
     * @param bo 操作日志对象
     */
    override fun insertOperlog(bo: SysOperLogBo) {
        val operLog = convert(bo, SysOperLogEntity::class.java)
        operLog.operTime = Date()
        baseMapper.insert(operLog)
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    override fun selectOperLogList(operLog: SysOperLogBo): List<SysOperLogVo> {
        val params: Map<String, Any?> = operLog.params
        return baseMapper.selectVoList(KtQueryWrapper(SysOperLogEntity::class.java)
            .like(StringUtils.isNotBlank(operLog.operIp), SysOperLogEntity::operIp, operLog.operIp)
            .like(StringUtils.isNotBlank(operLog.title), SysOperLogEntity::title, operLog.title)
            .eq(
                operLog.businessType != null && operLog.businessType!! > 0,
                SysOperLogEntity::businessType, operLog.businessType
            )
            .func { f: KtQueryWrapper<SysOperLogEntity> ->
                if (ArrayUtil.isNotEmpty(operLog.businessTypes)) {
                    f.`in`(SysOperLogEntity::businessType, listOf(*operLog.businessTypes))
                }
            }
            .eq(
                operLog.status != null && operLog.status!! > 0,
                SysOperLogEntity::status, operLog.status
            )
            .like(StringUtils.isNotBlank(operLog.operName), SysOperLogEntity::operName, operLog.operName)
            .between(
                params["beginTime"] != null && params["endTime"] != null,
                SysOperLogEntity::operTime, params["beginTime"], params["endTime"]
            )
            .orderByDesc(SysOperLogEntity::operId))
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    override fun deleteOperLogByIds(operIds: Array<Long>): Int {
        return baseMapper.deleteBatchIds(Arrays.asList(*operIds))
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    override fun selectOperLogById(operId: Long?): SysOperLogVo? {
        return baseMapper.selectVoById(operId)
    }

    /**
     * 清空操作日志
     */
    override fun cleanOperLog() {
        baseMapper.delete(KtQueryWrapper(SysOperLogEntity::class.java))
    }
}
