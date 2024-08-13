package com.zipper.framework.oss.constant

import com.zipper.framework.core.constant.GlobalConstants


/**
 * 对象存储常量
 *
 * @author Lion Li
 */
object OssConstant {
    /**
     * 默认配置KEY
     */
    val DEFAULT_CONFIG_KEY: String = GlobalConstants.GLOBAL_REDIS_KEY + "sys_oss:default_config"

    /**
     * 预览列表资源开关Key
     */
    const val PEREVIEW_LIST_RESOURCE_KEY: String = "sys.oss.previewListResource"

    /**
     * 系统数据ids
     */
    val SYSTEM_DATA_IDS: List<Long> = mutableListOf(1L, 2L, 3L, 4L)

    /**
     * 云服务商
     */
    val CLOUD_SERVICE: Array<String> = arrayOf("aliyun", "qcloud", "qiniu", "obs")

    /**
     * https 状态
     */
    const val IS_HTTPS: String = "Y"

}
