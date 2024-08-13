package com.zipper.framework.tanent.core

import cn.dev33.satoken.dao.SaTokenDao.NEVER_EXPIRE
import cn.dev33.satoken.dao.SaTokenDao.NOT_VALUE_EXPIRE
import com.zipper.framework.redis.utils.RedisUtils
import com.zipper.framework.satoken.core.dao.PlusSaTokenDao
import com.zipper.framework.core.constant.GlobalConstants
import java.time.Duration


/**
 * SaToken 认证数据持久层 适配多租户
 *
 * @author Lion Li
 */
class TenantSaTokenDao : PlusSaTokenDao() {
    override fun get(key: String): String? {
        return super.get(GlobalConstants.GLOBAL_REDIS_KEY + key)
    }

    override fun set(key: String, value: String, timeout: Long) {
        super.set(GlobalConstants.GLOBAL_REDIS_KEY + key, value, timeout)
    }

    /**
     * 修修改指定key-value键值对 (过期时间不变)
     */
    override fun update(key: String, value: String) {
        val expire = getTimeout(key)
        // -2 = 无此键
        if (expire == NOT_VALUE_EXPIRE) {
            return
        }
        this.set(key, value, expire)
    }

    /**
     * 删除Value
     */
    override fun delete(key: String) {
        super.delete(GlobalConstants.GLOBAL_REDIS_KEY + key)
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    override fun getTimeout(key: String): Long {
        return super.getTimeout(GlobalConstants.GLOBAL_REDIS_KEY + key)
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    override fun updateTimeout(key: String, timeout: Long) {
        // 判断是否想要设置为永久
        if (timeout == NEVER_EXPIRE) {
            val expire = getTimeout(key)
            if (expire == NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.get(key)?.let {
                    this.set(key, it, timeout)
                }
            }
            return
        }
        RedisUtils.expire(GlobalConstants.GLOBAL_REDIS_KEY + key, Duration.ofSeconds(timeout))
    }


    /**
     * 获取Object，如无返空
     */
    override fun getObject(key: String): Any? {
        return super.getObject(GlobalConstants.GLOBAL_REDIS_KEY + key)
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    override fun setObject(key: String, value: Any, timeout: Long) {
        super.setObject(GlobalConstants.GLOBAL_REDIS_KEY + key, value, timeout)
    }

    /**
     * 更新Object (过期时间不变)
     */
    override fun updateObject(key: String, `object`: Any) {
        val expire = getObjectTimeout(key)
        // -2 = 无此键
        if (expire == NOT_VALUE_EXPIRE) {
            return
        }
        this.setObject(key, `object`, expire)
    }

    /**
     * 删除Object
     */
    override fun deleteObject(key: String) {
        super.deleteObject(GlobalConstants.GLOBAL_REDIS_KEY + key)
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    override fun getObjectTimeout(key: String): Long {
        return super.getObjectTimeout(GlobalConstants.GLOBAL_REDIS_KEY + key)
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    override fun updateObjectTimeout(key: String, timeout: Long) {
        // 判断是否想要设置为永久
        if (timeout == NEVER_EXPIRE) {
            val expire = getObjectTimeout(key)
            if (expire == NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.getObject(key)?.let { this.setObject(key, it, timeout) }
            }
            return
        }
        RedisUtils.expire(GlobalConstants.GLOBAL_REDIS_KEY + key, Duration.ofSeconds(timeout))
    }


    /**
     * 搜索数据
     */
    override fun searchData(prefix: String, keyword: String, start: Int, size: Int, sortType: Boolean): List<String> {
        return super.searchData(GlobalConstants.GLOBAL_REDIS_KEY + prefix, keyword, start, size, sortType)
    }
}
