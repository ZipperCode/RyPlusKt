package com.zipper.modules.system.listener

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.crypto.digest.BCrypt
import cn.hutool.extra.spring.SpringUtil
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.event.AnalysisEventListener
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.modules.system.domain.bo.SysUserBo
import com.zipper.modules.system.domain.vo.SysUserImportVo
import com.zipper.modules.system.service.config.ISysConfigService
import com.zipper.modules.system.service.user.ISysUserService
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.ValidatorUtils
import org.zipper.framework.excel.core.ExcelListener
import org.zipper.framework.excel.core.ExcelResult

/**
 * 系统用户自定义导入
 *
 * @author Lion Li
 */
class SysUserImportListener(
    isUpdateSupport: Boolean
) : AnalysisEventListener<SysUserImportVo>(), ExcelListener<SysUserImportVo> {
    private val userService: ISysUserService

    private val password: String

    private val isUpdateSupport: Boolean

    private val operUserId: Long

    private var successNum = 0
    private var failureNum = 0
    private val successMsg = StringBuilder()
    private val failureMsg = StringBuilder()

    init {
        val initPassword = SpringUtil.getBean(ISysConfigService::class.java).selectConfigByKey("sys.user.initPassword")
        this.userService = SpringUtil.getBean(ISysUserService::class.java)
        this.password = BCrypt.hashpw(initPassword)
        this.isUpdateSupport = isUpdateSupport
        this.operUserId = LoginHelper.getUserId()
    }

    override fun invoke(userVo: SysUserImportVo, context: AnalysisContext) {
        val sysUser = userService.selectUserByUserName(userVo.userName)
        try {
            // 验证是否存在这个用户
            if (ObjectUtil.isNull(sysUser)) {
                val user = BeanUtil.toBean(userVo, SysUserBo::class.java)
                ValidatorUtils.validate(user)
                user.password = password
                user.createBy = operUserId
                userService.insertUser(user)
                successNum++
                successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.userName).append(" 导入成功")
            } else if (isUpdateSupport) {
                val userId = sysUser!!.userId
                val user = BeanUtil.toBean(userVo, SysUserBo::class.java)
                user.userId = userId
                ValidatorUtils.validate(user)
                userService.checkUserAllowed(user.userId)
                userService.checkUserDataScope(user.userId)
                user.updateBy = operUserId
                userService.updateUser(user)
                successNum++
                successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.userName).append(" 更新成功")
            } else {
                failureNum++
                failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(sysUser!!.userName).append(" 已存在")
            }
        } catch (e: Exception) {
            failureNum++
            val msg = "<br/>" + failureNum + "、账号 " + userVo.userName + " 导入失败："
            failureMsg.append(msg).append(e.message)
            log.error(msg, e)
        }
    }

    override fun doAfterAllAnalysed(context: AnalysisContext) {
    }

    override fun getExcelResult(): ExcelResult<SysUserImportVo> {
        return object : ExcelResult<SysUserImportVo> {
            override fun getAnalysis(): String {
                if (failureNum > 0) {
                    failureMsg.insert(0, "很抱歉，导入失败！共 $failureNum 条数据格式不正确，错误如下：")
                    throw ServiceException(failureMsg.toString())
                } else {
                    successMsg.insert(0, "恭喜您，数据已全部导入成功！共 $successNum 条，数据如下：")
                }
                return successMsg.toString()
            }

            override fun getList(): List<SysUserImportVo> = emptyList()

            override fun getErrorList(): List<String> = emptyList()
        }
    }
}
