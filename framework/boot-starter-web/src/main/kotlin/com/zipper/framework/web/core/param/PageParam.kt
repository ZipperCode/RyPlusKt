package com.zipper.framework.web.core.param

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import lombok.Data
import java.io.Serializable

/**
 * 分页查询参数
 */
@Schema(description = "分页参数")
@Data
open class PageParam : Serializable {
    @Schema(description = "页码，从 1 开始", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    var pageNo: Int = PAGE_NO

    @Schema(description = "每页条数，最大值为 100", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(
        value = 100,
        message = "每页条数最大值为 100"
    )
    var pageSize: Int = PAGE_SIZE

    companion object {
        private const val PAGE_NO = 1
        private const val PAGE_SIZE = 10

        /**
         * 每页条数 - 不分页
         *
         * 例如说，导出接口，可以设置 [.pageSize] 为 -1 不分页，查询所有数据。
         */
        const val PAGE_SIZE_NONE: Int = -1
    }
}
