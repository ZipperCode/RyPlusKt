package com.zipper.modules.storage.client.db

import com.zipper.framework.core.annotation.NoArgs
import com.zipper.modules.storage.client.FileClientConfig
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.URL

/**
 * 数据库文件存储配置
 */
@NoArgs
data class DataBaseFileClientConfig(
    /**
     * 域名
     */
    @field:NotEmpty(message = "domain 不能为空")
    @field:URL(message = "domain 必须是 URL 格式")
    var domain: String = ""
) : FileClientConfig {

    override fun getCustomDomain(): String {
        return domain
    }
}