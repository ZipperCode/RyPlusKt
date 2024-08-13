package com.zipper.server.web.service.impl

import cn.dev33.satoken.stp.SaLoginModel
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.map.MapUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.http.HttpUtil
import cn.hutool.http.Method
import com.zipper.framework.core.domain.model.SocialLoginBody
import com.zipper.framework.core.enums.UserStatus
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.exception.user.UserException
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.ValidatorUtils.validate
import com.zipper.framework.json.utils.JsonUtils.parseObject
import com.zipper.framework.mybatis.core.MybatisKt
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.satoken.utils.LoginHelper.login
import com.zipper.framework.social.config.properties.SocialProperties
import com.zipper.framework.social.utils.SocialUtils.loginAuth
import com.zipper.framework.tanent.helper.TenantHelper
import com.zipper.modules.system.domain.entity.SysClientEntity
import com.zipper.modules.system.domain.entity.SysUserEntity
import com.zipper.modules.system.domain.vo.SysSocialVo
import com.zipper.modules.system.domain.vo.SysUserVo
import com.zipper.modules.system.mapper.SysUserMapper
import com.zipper.modules.system.service.social.ISysSocialService
import com.zipper.server.web.domain.vo.LoginVo
import com.zipper.server.web.service.IAuthStrategy
import com.zipper.server.web.service.SysLoginService
import org.springframework.stereotype.Service

/**
 * 第三方授权策略
 *
 * @author thiszhc is 三三
 */
@Service("social" + IAuthStrategy.BASE_NAME)
class SocialAuthStrategy(
    private val socialProperties: SocialProperties,
    private val sysSocialService: ISysSocialService,
    private val userMapper: SysUserMapper,
    private val loginService: SysLoginService
) : IAuthStrategy {
    /**
     * 登录-第三方授权登录
     *
     * @param body     登录信息
     * @param client   客户端信息
     */
    override fun login(body: String, client: SysClientEntity): LoginVo {
        val loginBody = parseObject(body, SocialLoginBody::class.java)!!
        validate(loginBody)
        val response = loginAuth(
            loginBody.source, loginBody.socialCode,
            loginBody.socialState, socialProperties
        )
        if (!response.ok()) {
            throw ServiceException(response.msg)
        }
        val authUserData = response.data
        if ("GITEE" == authUserData.source) {
            // 如用户使用 gitee 登录顺手 star 给作者一点支持 拒绝白嫖
            HttpUtil.createRequest(Method.PUT, "https://gitee.com/api/v5/user/starred/dromara/RuoYi-Vue-Plus")
                .formStr(MapUtil.of("access_token", authUserData.token.accessToken))
                .executeAsync()
            HttpUtil.createRequest(Method.PUT, "https://gitee.com/api/v5/user/starred/dromara/RuoYi-Cloud-Plus")
                .formStr(MapUtil.of("access_token", authUserData.token.accessToken))
                .executeAsync()
        }

        val list: List<SysSocialVo?> = sysSocialService.selectByAuthId(authUserData.source + authUserData.uuid)
        if (CollUtil.isEmpty(list)) {
            throw ServiceException("你还没有绑定第三方账号，绑定后才可以登录！")
        }
        val social = list.firstOrNull{ it?.tenantId == loginBody.tenantId} ?: throw ServiceException("对不起，你没有权限登录当前租户！")
        // 查找用户
        val user = loadUser(social.tenantId!!, social.userId)

        // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
        val loginUser = loginService.buildLoginUser(user)
        loginUser.clientKey = client.clientKey
        loginUser.deviceType = client.deviceType
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
        return loginVo
    }

    private fun loadUser(tenantId: String, userId: Long?): SysUserVo {
        return TenantHelper.dynamic(tenantId) {
            val user = userMapper.selectOne(
                MybatisKt.ktQuery<SysUserEntity>()
                    .select(SysUserEntity::userName, SysUserEntity::status)
                    .eq(SysUserEntity::userId, userId)
            )
            if (ObjectUtil.isNull(user)) {
                log.info("登录用户：{} 不存在.", "")
                throw UserException("user.not.exists", "")
            } else if (UserStatus.DISABLE.code == user.status) {
                log.info("登录用户：{} 已被停用.", "")
                throw UserException("user.blocked", "")
            }
            userMapper.selectUserByUserName(user.userName)!!
        }
    }
}
