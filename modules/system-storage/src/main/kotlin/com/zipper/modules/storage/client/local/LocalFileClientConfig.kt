package com.zipper.modules.storage.client.local

import com.zipper.framework.core.annotation.NoArgs
import com.zipper.modules.storage.client.FileClientConfig
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.URL

@NoArgs
data class LocalFileClientConfig(
    /**
     * 基础路径
     */
    @field:NotEmpty(message = "基础路径不能为空")
    var basePath: String = "",

    /**
     * 自定义域名
     */
    @field:NotEmpty(message = "domain 不能为空")
    @field:URL(message = "domain 必须是 URL 格式")
    var domain: String = ""
) : FileClientConfig {

    override fun getCustomDomain(): String {
        return domain
    }
}