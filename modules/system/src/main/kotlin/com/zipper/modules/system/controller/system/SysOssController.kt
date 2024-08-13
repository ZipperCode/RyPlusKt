package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.hutool.core.util.ObjectUtil
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysOssBo
import com.zipper.modules.system.domain.vo.SysOssUploadVo
import com.zipper.modules.system.domain.vo.SysOssVo
import com.zipper.modules.system.service.oss.ISysOssService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import com.zipper.framework.core.validate.QueryGroup
import java.io.IOException
import java.util.*


/**
 * 文件上传 控制层
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/resource/oss")
class SysOssController(private val ossService: ISysOssService) : BaseController() {
    /**
     * 查询OSS对象存储列表
     */
    @SaCheckPermission("system:oss:list")
    @GetMapping("/list")
    fun list(@Validated(QueryGroup::class) bo: SysOssBo, pageQuery: PageQuery): TableDataInfo<SysOssVo> {
        return ossService.queryPageList(bo, pageQuery)
    }

    /**
     * 查询OSS对象基于id串
     *
     * @param ossIds OSS对象ID串
     */
    @SaCheckPermission("system:oss:list")
    @GetMapping("/listByIds/{ossIds}")
    fun listByIds(@PathVariable ossIds: Array<Long>): R<List<SysOssVo>> {
        val list = ossService.listByIds(Arrays.asList(*ossIds))
        return R.ok(list)
    }

    /**
     * 上传OSS对象存储
     *
     * @param file 文件
     */
    @SaCheckPermission("system:oss:upload")
    @Log(title = "OSS对象存储", businessType = BusinessType.INSERT)
    @PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(@RequestPart("file") file: MultipartFile?): R<SysOssUploadVo> {
        if (ObjectUtil.isNull(file)) {
            return R.fail("上传文件不能为空")
        }
        val oss = ossService.upload(file!!)
        val uploadVo = SysOssUploadVo()
        uploadVo.url = oss.url
        uploadVo.fileName = oss.originalName
        uploadVo.ossId = oss.ossId.toString()
        return R.ok(uploadVo)
    }

    /**
     * 下载OSS对象
     *
     * @param ossId OSS对象ID
     */
    @SaCheckPermission("system:oss:download")
    @GetMapping("/download/{ossId}")
    @Throws(IOException::class)
    fun download(@PathVariable ossId: Long?, response: HttpServletResponse?) {
        ossService.download(ossId, response!!)
    }

    /**
     * 删除OSS对象存储
     *
     * @param ossIds OSS对象ID串
     */
    @SaCheckPermission("system:oss:remove")
    @Log(title = "OSS对象存储", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossIds}")
    fun remove(@PathVariable ossIds: Array<Long>): R<Void> {
        return toAjax(ossService.deleteWithValidByIds(listOf(*ossIds), true))
    }
}
