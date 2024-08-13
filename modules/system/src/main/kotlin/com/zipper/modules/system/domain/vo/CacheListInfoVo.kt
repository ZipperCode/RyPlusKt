package com.zipper.modules.system.domain.vo

import lombok.Data
import java.util.*

/**
 * 缓存监控列表信息
 *
 * @author Michelle.Chung
 */
@Data
class CacheListInfoVo {
    var info: Properties? = null

    var dbSize: Long? = null

    var commandStats: List<Map<String, String>>? = null
}
