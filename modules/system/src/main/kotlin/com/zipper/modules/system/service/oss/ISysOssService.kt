package com.zipper.modules.system.service.oss

import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.modules.system.domain.bo.SysOssBo
import com.zipper.modules.system.domain.vo.SysOssVo
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException

/**
 * 文件上传 服务层
 *
 * @author Lion Li
 */
interface ISysOssService {
    fun queryPageList(sysOss: SysOssBo, pageQuery: PageQuery): TableDataInfo<SysOssVo>

    fun listByIds(ossIds: Collection<Long>): List<SysOssVo>

    fun getById(ossId: Long?): SysOssVo?

    fun upload(file: MultipartFile): SysOssVo

    fun upload(file: File): SysOssVo

    @Throws(IOException::class)
    fun download(ossId: Long?, response: HttpServletResponse)

    fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean
}
