package com.zipper.modules.storage.client.s3

import com.zipper.framework.core.annotation.NoArgs
import com.zipper.framework.core.validate.AddGroup
import com.zipper.framework.core.validate.EditGroup
import com.zipper.modules.storage.client.FileClientConfig
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

@NoArgs
data class S3FileClientConfig(
    /**
     * 节点地址
     * 1. MinIO：https://www.iocoder.cn/Spring-Boot/MinIO 。例如说，http://127.0.0.1:9000
     * 2. 阿里云：https://help.aliyun.com/document_detail/31837.html
     * 3. 腾讯云：https://cloud.tencent.com/document/product/436/6224
     * 4. 七牛云：https://developer.qiniu.com/kodo/4088/s3-access-domainname
     * 5. 华为云：https://developer.huaweicloud.com/endpoint?OBS
     */
    @field:NotBlank(message = "访问站点不能为空", groups = [AddGroup::class, EditGroup::class])
    @field:Size(
        min = 2,
        max = 100,
        message = "endpoint长度必须介于{min}和{max}之间"
    )
    val endpoint: String,
    /**
     * 自定义域名
     * 1. MinIO：通过 Nginx 配置
     * 2. 阿里云：https://help.aliyun.com/document_detail/31836.html
     * 3. 腾讯云：https://cloud.tencent.com/document/product/436/11142
     * 4. 七牛云：https://developer.qiniu.com/kodo/8556/set-the-custom-source-domain-name
     * 5. 华为云：https://support.huaweicloud.com/usermanual-obs/obs_03_0032.html
     */
    @field:NotNull(message = "domain 不能为空")
    @field:URL(message = "domain 必须是 URL 格式")
    val domain: String,
    /**
     * 存储 Bucket
     */
    @field:NotBlank(message = "桶名称不能为空", groups = [AddGroup::class, EditGroup::class])
    @field:Size(
        min = 2,
        max = 100,
        message = "bucketName长度必须介于{min}和{max}之间"
    )
    val bucket: String,
    /**
     * 访问 Key
     * 1. MinIO：https://www.iocoder.cn/Spring-Boot/MinIO
     * 2. 阿里云：https://ram.console.aliyun.com/manage/ak
     * 3. 腾讯云：https://console.cloud.tencent.com/cam/capi
     * 4. 七牛云：https://portal.qiniu.com/user/key
     * 5. 华为云：https://support.huaweicloud.com/qs-obs/obs_qs_0005.html
     */
    @field:NotBlank(message = "accessKey不能为空", groups = [AddGroup::class, EditGroup::class])
    @field:Size(
        min = 2,
        max = 100,
        message = "accessKey长度必须介于{min}和{max} 之间"
    )
    val accessKey: String,
    /**
     * 访问 Secret
     */
    @field:NotBlank(message = "secretKey不能为空", groups = [AddGroup::class, EditGroup::class])
    @field:Size(
        min = 2,
        max = 100,
        message = "secretKey长度必须介于{min}和{max} 之间"
    )
    val accessSecret: String,
    /**
     * 区域
     */
    @field:NotNull(message = "region 不能为空")
    val region: String,
    /**
     * 前缀
     */
    val prefix: String = "",
    /**
     * 是否https（0否 1是）
     */
    val useHttps: String = "N",
    /**
     * 桶权限类型(0private 1public 2custom)
     */
    val accessPolicy: String = "private",
    /**
     * 服务提供商
     */
    var service: String = "未知"
) : FileClientConfig {
    override fun getCustomDomain(): String {
        return domain
    }
}