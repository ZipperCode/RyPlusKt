package com.zipper.framework.mybatis.handler

import cn.hutool.core.util.ObjectUtil
import cn.hutool.http.HttpStatus
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
import com.zipper.framework.core.domain.model.LoginUser
import com.zipper.framework.mybatis.core.domain.BaseEntity
import com.zipper.framework.satoken.utils.LoginHelper
import org.apache.ibatis.reflection.MetaObject
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.ktext.withType
import com.zipper.framework.mybatis.core.domain.BaseEntity2
import com.zipper.framework.mybatis.core.domain.CreatorMixin
import com.zipper.framework.mybatis.core.domain.UpdaterMixin
import java.time.LocalDateTime
import java.util.*

/**
 * MP注入处理器
 *
 * @author Lion Li
 * @date 2021/4/25
 */
class InjectionMetaObjectHandler : MetaObjectHandler {
    override fun insertFill(metaObject: MetaObject) {
        try {
            metaObject.originalObject.withType<BaseEntity> {
                val baseEntity = this
                val current = baseEntity.createTime ?: Date()
                baseEntity.createTime = current
                baseEntity.updateTime = current

                val loginUser = getLoginUser() ?: return

                val userId = baseEntity.createBy ?: loginUser.userId
                // 当前已登录 且 创建人为空 则填充
                baseEntity.createBy = userId
                // 当前已登录 且 更新人为空 则填充
                baseEntity.updateBy = userId
                baseEntity.createDept = baseEntity.createDept ?: loginUser.deptId
            }

            metaObject.originalObject.withType<CreatorMixin> {
                val current = createTime ?: LocalDateTime.now()
                createTime = current
                val loginUser = getLoginUser() ?: return
                val username = createBy ?: loginUser.username
                createBy = username
            }
            metaObject.originalObject.withType<UpdaterMixin> {
                updateTime = updateTime ?: LocalDateTime.now()
                updateBy = updateBy ?: getLoginUser()?.username
            }

        } catch (e: Exception) {
            throw ServiceException("自动注入异常 => " + e.message, HttpStatus.HTTP_UNAUTHORIZED)
        }
    }

    override fun updateFill(metaObject: MetaObject) {
        try {
            metaObject.originalObject.withType<BaseEntity> {
                val baseEntity = this
                // 更新时间填充(不管为不为空)
                baseEntity.updateTime = Date()

                val loginUser = getLoginUser() ?: return
                if (ObjectUtil.isNotNull(loginUser)) {
                    // 当前已登录 更新人填充(不管为不为空)
                    baseEntity.updateBy = loginUser.userId
                }
            }

            metaObject.originalObject.withType<BaseEntity2> {
                updateTime = LocalDateTime.now()
                val loginUser = getLoginUser() ?: return
                updateBy = loginUser.username
            }

        } catch (e: Exception) {
            throw ServiceException("自动注入异常 => " + e.message, HttpStatus.HTTP_UNAUTHORIZED)
        }
    }

    /**
     * 获取登录用户名
     */
    private fun getLoginUser(): LoginUser? {
        try {
            return LoginHelper.getLoginUser()
        } catch (e: java.lang.Exception) {
            log.warn("自动注入警告 => 用户未登录")
            return null
        }
    }
}
