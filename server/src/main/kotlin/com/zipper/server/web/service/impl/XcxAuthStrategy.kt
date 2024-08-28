package com.zipper.server.web.service.impl

import cn.dev33.satoken.stp.SaLoginModel
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.ObjectUtil
import com.zipper.framework.core.domain.model.XcxLoginBody
import com.zipper.framework.core.domain.model.XcxLoginUser
import com.zipper.framework.core.enums.UserStatus
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.ValidatorUtils.validate
import com.zipper.framework.json.utils.JsonUtils.parseObject
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.satoken.utils.LoginHelper.login
import com.zipper.modules.auth.domain.entity.SysClientEntity
import com.zipper.modules.system.domain.vo.SysUserVo
import com.zipper.server.web.domain.vo.LoginVo
import com.zipper.server.web.service.IAuthStrategy
import com.zipper.server.web.service.SysLoginService
import org.springframework.stereotype.Service

/**
 * 邮件认证策略
 *
 * @author Michelle.Chung
 */
@Service("xcx" + IAuthStrategy.BASE_NAME)
class XcxAuthStrategy(private val loginService: SysLoginService) : IAuthStrategy {
    override fun login(body: String, client: SysClientEntity): LoginVo {
        val loginBody = parseObject(body, XcxLoginBody::class.java)
        validate(loginBody)
        // xcxCode 为 小程序调用 wx.login 授权后获取
        val xcxCode = loginBody.xcxCode
        // 多个小程序识别使用
        val appid = loginBody.appid

        // todo 以下自行实现
        // 校验 appid + appsrcret + xcxCode 调用登录凭证校验接口 获取 session_key 与 openid
        val openid = ""
        // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
        val user = loadUserByOpenid(openid)

        // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
        val loginUser = XcxLoginUser()
        loginUser.tenantId = user.tenantId
        loginUser.userId = user.userId
        loginUser.username = user.userName
        loginUser.nickname = user.nickName
        loginUser.userType = user.userType
        loginUser.clientKey = client.clientKey
        loginUser.deviceType = client.deviceType
        loginUser.openid = openid

        val model = SaLoginModel()
        model.setDevice(client.deviceType)
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.timeout!!)
        model.setActiveTimeout(client.activeTimeout!!)
        model.setExtra(LoginHelper.CLIENT_KEY, client.clientId)
        // 生成token
        login(loginUser, model)

        val loginVo = LoginVo()
        loginVo.accessToken = StpUtil.getTokenValue()
        loginVo.expireIn = StpUtil.getTokenTimeout()
        loginVo.clientId = client.clientId
        loginVo.openid = openid
        return loginVo
    }

    private fun loadUserByOpenid(openid: String): SysUserVo {
        // 使用 openid 查询绑定用户 如未绑定用户 则根据业务自行处理 例如 创建默认用户
        // todo 自行实现 userService.selectUserByOpenid(openid);
        val user = SysUserVo()
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", openid)
            // todo 用户不存在 业务逻辑自行实现
        } else if (UserStatus.DISABLE.code == user.status) {
            log.info("登录用户：{} 已被停用.", openid)
            // todo 用户已被停用 业务逻辑自行实现
        }
        return user
    }
}
