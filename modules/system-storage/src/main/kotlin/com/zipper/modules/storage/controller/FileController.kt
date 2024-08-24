package com.zipper.modules.storage.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaIgnore
import cn.hutool.core.util.StrUtil
import cn.hutool.core.util.URLUtil
import com.zipper.framework.core.domain.R
import com.zipper.framework.core.ext.log
import com.zipper.framework.core.utils.MapstructUtils
import com.zipper.framework.core.utils.file.FileUtilsExt
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.storage.domain.param.FileRecordPageParam
import com.zipper.modules.storage.domain.param.FileUploadParam
import com.zipper.modules.storage.domain.vo.FileRecordVo
import com.zipper.modules.storage.service.FileRecordService
import com.zipper.modules.storage.service.FileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "管理后台 - 文件存储")
@RestController
@RequestMapping("/store/file")
@Validated
class FileController(
    private val fileService: FileService,
    private val fileRecordService: FileRecordService,
) : BaseController() {

    @SaCheckPermission("store:file:upload")
    @Log(title = "文件存储-上传", businessType = BusinessType.INSERT)
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    @PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(@Valid fileUploadParam: FileUploadParam): R<FileRecordVo> {
        if (fileUploadParam.file.isEmpty) {
            return R.fail("上传文件不能为空")
        }
        val createBo = fileService.createFile(fileUploadParam.file, fileUploadParam.relativePath)
        return success(fileRecordService.create(createBo))
    }

    @SaCheckPermission("system:file:remove")
    @DeleteMapping("/delete")
    fun delete(@RequestParam("ids") recordIds: Array<Long>): R<Void> {
        if (recordIds.isEmpty()) {
            return R.fail("id不能为空")
        }

        val deleteList = fileRecordService.delete(recordIds)
        for (fileRecordEntity in deleteList) {
            if (fileRecordEntity.configId != null && fileRecordEntity.path != null) {
                kotlin.runCatching {
                    fileService.deleteFile(fileRecordEntity.configId!!, fileRecordEntity.path!!)
                }.onFailure {
                    log.error("删除文件失败", it)
                }
            }
        }

        return R.ok()
    }

    @SaCheckPermission("store:file:download")
    @Operation(summary = "下载文件", description = "模式二：前端下载文件")
    @GetMapping("/download/{configId}/**")
    fun download(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @PathVariable("configId") configId: Long
    ) {
        // 获取请求的路径
        var path = StrUtil.subAfter(request.requestURI, "/download/", false)
        require(!StrUtil.isEmpty(path)) { "结尾的 path 路径必须传递" }
        // 解码，解决中文路径的问题 https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/807/
        path = URLUtil.decode(path)
        kotlin.runCatching {
            val content = fileService.getFileContent(configId, path)
            FileUtilsExt.writeAttachment(response, path, content)
        }.onFailure {
            log.warn("下载文件失败 configId={} path={}", configId, path)
            log.error("下载文件失败", it)
            response.status = HttpStatus.NOT_FOUND.value()
        }
    }

    @SaIgnore
    @Operation(summary = "请求文件", description = "获取上传文件内容")
    @GetMapping("/{configId}/**")
    fun getFile(request: HttpServletRequest, response: HttpServletResponse, @PathVariable("configId") configId: Long) {
        val filePath = URLUtil.decode(StrUtil.subAfter(request.requestURI, "/${configId}/", false))
        if (filePath.isEmpty()) {
            response.status = HttpStatus.NOT_FOUND.value()
            return
        }
        runCatching {
            val content = fileService.getFileContent(configId, filePath)
            FileUtilsExt.writeAttachment(response, filePath, content)
        }.onFailure {
            log.warn("获取文件内容失败 url = {}", request.requestURI)
            log.error("获取文件内容失败", it)
            response.status = HttpStatus.NOT_FOUND.value()
        }
    }

    @SaCheckPermission("store:file:list")
    @GetMapping("/list")
    fun pageList(@Valid param: FileRecordPageParam): TableDataInfo<FileRecordVo> {
        return fileRecordService.pageList(param)
    }

    /**
     *
     */
    @SaCheckPermission("store:file:list")
    @GetMapping("/listByIds")
    fun listByIds(@RequestParam("ids") recordIds: Array<Long>): R<List<FileRecordVo>> {
        val list = fileRecordService.queryByIds(recordIds)
        return R.ok(MapstructUtils.convertWithClass(list, FileRecordVo::class.java))
    }
}