package com.zipper.framework.satoken.core.dao

import cn.dev33.satoken.dao.SaTokenDao
import cn.dev33.satoken.util.SaFoxUtil
import com.zipper.framework.redis.utils.RedisUtils
import java.time.Duration

/**
 * Sa-Token持久层接口(使用框架自带RedisUtils实现 协议统一)
 *
 * @author Lion Li
 */
open class PlusSaTokenDao : SaTokenDao {
    /**
     * 获取Value，如无返空
     */
    override fun get(key: String): String? {
        return RedisUtils.getCacheObject(key)
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    override fun set(key: String, value: String, timeout: Long) {
        if (timeout == 0L || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return
        }
        // 判断是否为永不过期
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            RedisUtils.setCacheObject(key, value)
        } else {
            RedisUtils.setCacheObject(key, value, Duration.ofSeconds(timeout))
        }
    }

    /**
     * 修修改指定key-value键值对 (过期时间不变)
     */
    override fun update(key: String, value: String) {
        if (RedisUtils.hasKey(key)) {
            RedisUtils.setCacheObject(key, value, true)
        }
    }

    /**
     * 删除Value
     */
    override fun delete(key: String) {
        RedisUtils.deleteObject(key)
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    override fun getTimeout(key: String): Long {
        val timeout = RedisUtils.getTimeToLive<Long>(key)
        return if (timeout < 0) timeout else timeout / 1000
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    override fun updateTimeout(key: String, timeout: Long) {
        RedisUtils.expire(key, Duration.ofSeconds(timeout))
    }


    /**
     * 获取Object，如无返空
     */
    override fun getObject(key: String): Any? {
        return RedisUtils.getCacheObject(key)
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    override fun setObject(key: String, value: Any, timeout: Long) {
        if (timeout == 0L || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return
        }
        // 判断是否为永不过期
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            RedisUtils.setCacheObject(key, value)
        } else {
            RedisUtils.setCacheObject(key, value, Duration.ofSeconds(timeout))
        }
    }

    /**
     * 更新Object (过期时间不变)
     */
    override fun updateObject(key: String, `object`: Any) {
        if (RedisUtils.hasKey(key)) {
            RedisUtils.setCacheObject(key, `object`, true)
        }
    }

    /**
     * 删除Object
     */
    override fun deleteObject(key: String) {
        RedisUtils.deleteObject(key)
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    override fun getObjectTimeout(key: String): Long {
        val timeout: Long = RedisUtils.getTimeToLive<Long>(key)
        return if (timeout < 0) timeout else timeout / 1000
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    override fun updateObjectTimeout(key: String, timeout: Long) {
        RedisUtils.expire(key, Duration.ofSeconds(timeout))
    }


    /**
     * 搜索数据
     */
    override fun searchData(prefix: String, keyword: String, start: Int, size: Int, sortType: Boolean): List<String> {
        val keys: Collection<String> = RedisUtils.keys("$prefix*$keyword*")
        val list: List<String> = ArrayList(keys)
        return SaFoxUtil.searchList(list, start, size, sortType)
    }
}
