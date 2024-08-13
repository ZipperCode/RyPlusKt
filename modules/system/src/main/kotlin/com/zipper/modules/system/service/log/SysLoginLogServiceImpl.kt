package com.zipper.modules.system.service.log

import cn.hutool.core.util.ObjectUtil
import cn.hutool.extra.servlet.JakartaServletUtil
import cn.hutool.http.useragent.UserAgentUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.log.event.LoginLogEvent
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.modules.system.domain.bo.SysLoginLogBo
import com.zipper.modules.system.domain.entity.SysClientEntity
import com.zipper.modules.system.domain.entity.SysLoginLogEntity
import com.zipper.modules.system.domain.vo.SysLoginLogVo
import com.zipper.modules.system.mapper.SysLoginLogMapper
import com.zipper.modules.system.service.client.ISysClientService
import org.apache.commons.lang3.StringUtils
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import com.zipper.framework.core.constant.Constants
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.framework.core.utils.ip.AddressUtils
import java.util.*

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author Lion Li
 */
@Service
class SysLoginLogServiceImpl(
    private val baseMapper: SysLoginLogMapper,
    private val clientService: ISysClientService
) :
    ISysLoginLogService {
    /**
     * 记录登录信息
     *
     * @param loginLogEvent 登录事件
     */
    @Async
    @EventListener
    fun recordLoginLog(loginLogEvent: LoginLogEvent) {
        val request = loginLogEvent.request ?: return
        val userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"))
        val ip = JakartaServletUtil.getClientIP(request)
        // 客户端信息
        val clientid = request.getHeader(LoginHelper.CLIENT_KEY)
        var client: SysClientEntity? = null
        if (StringUtils.isNotBlank(clientid)) {
            client = clientService.queryByClientId(clientid)
        }

        val address: String = AddressUtils.getRealAddressByIP(ip)
        val s = StringBuilder()
        s.append(getBlock(ip))
        s.append(address)
        s.append(getBlock(loginLogEvent.username))
        s.append(getBlock(loginLogEvent.status))
        s.append(getBlock(loginLogEvent.message))
        // 打印信息到日志
        log.info(s.toString(), loginLogEvent.args)
        // 获取客户端操作系统
        val os = userAgent.os.name
        // 获取客户端浏览器
        val browser = userAgent.browser.name
        // 封装对象
        val LoginLog = SysLoginLogBo()
        LoginLog.tenantId = loginLogEvent.tenantId
        LoginLog.userName = loginLogEvent.username
        if (ObjectUtil.isNotNull(client)) {
            LoginLog.clientKey = client!!.clientKey
            LoginLog.deviceType = client.deviceType
        }
        LoginLog.ipaddr = ip
        LoginLog.loginLocation = address
        LoginLog.browser = browser
        LoginLog.os = os
        LoginLog.msg = loginLogEvent.message
        // 日志状态
        if (StringUtils.equalsAny(loginLogEvent.status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            LoginLog.status = Constants.SUCCESS
        } else if (Constants.LOGIN_FAIL == loginLogEvent.status) {
            LoginLog.status = Constants.FAIL
        }
        // 插入数据
        insertLoginLog(LoginLog)
    }

    private fun getBlock(msg: Any?): String {
        return "[$msg]"
    }

    override fun selectPageLoginLogList(loginLog: SysLoginLogBo, pageQuery: PageQuery): TableDataInfo<SysLoginLogVo> {
        val params: Map<String, Any?> = loginLog.params
        val lqw = KtQueryWrapper(SysLoginLogEntity::class.java)
            .like(StringUtils.isNotBlank(loginLog.ipaddr), SysLoginLogEntity::ipaddr, loginLog.ipaddr)
            .eq(StringUtils.isNotBlank(loginLog.status), SysLoginLogEntity::status, loginLog.status)
            .like(StringUtils.isNotBlank(loginLog.userName), SysLoginLogEntity::userName, loginLog.userName)
            .between(
                params["beginTime"] != null && params["endTime"] != null,
                SysLoginLogEntity::loginTime, params["beginTime"], params["endTime"]
            )
        if (StringUtils.isBlank(pageQuery.orderByColumn)) {
            pageQuery.orderByColumn = "info_id"
            pageQuery.isAsc = "desc"
        }
        val page = baseMapper.selectVoPage<Page<SysLoginLogVo>>(pageQuery.build(), lqw)
        return TableDataInfo.build(page)
    }

    /**
     * 新增系统登录日志
     *
     * @param bo 访问日志对象
     */
    override fun insertLoginLog(bo: SysLoginLogBo) {
        val loginLog = convert(bo, SysLoginLogEntity::class.java)
        loginLog.loginTime = Date()
        baseMapper.insert(loginLog)
    }

    /**
     * 查询系统登录日志集合
     *
     * @param loginLogBo 访问日志对象
     * @return 登录记录集合
     */
    override fun selectLoginLogList(loginLogBo: SysLoginLogBo): List<SysLoginLogVo> {
        val params: Map<String, Any?> = loginLogBo.params
        return baseMapper.selectVoList(
            KtQueryWrapper(SysLoginLogEntity::class.java)
                .like(StringUtils.isNotBlank(loginLogBo.ipaddr), SysLoginLogEntity::ipaddr, loginLogBo.ipaddr)
                .eq(StringUtils.isNotBlank(loginLogBo.status), SysLoginLogEntity::status, loginLogBo.status)
                .like(StringUtils.isNotBlank(loginLogBo.userName), SysLoginLogEntity::userName, loginLogBo.userName)
                .between(
                    params["beginTime"] != null && params["endTime"] != null,
                    SysLoginLogEntity::loginTime, params["beginTime"], params["endTime"]
                )
                .orderByDesc(SysLoginLogEntity::infoId)
        )
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    override fun deleteLoginLogByIds(infoIds: Array<Long>): Int {
        return baseMapper.deleteBatchIds(listOf(*infoIds))
    }

    /**
     * 清空系统登录日志
     */
    override fun cleanLoginLog() {
        baseMapper.delete(KtQueryWrapper(SysLoginLogEntity::class.java))
    }
}
