package com.zipper.modules.system.service.oss

import cn.hutool.core.convert.Convert
import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.ObjectUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.oss.core.OssClient
import com.zipper.framework.oss.entity.UploadResult
import com.zipper.framework.oss.enumd.AccessPolicyType
import com.zipper.framework.oss.factory.OssFactory
import com.zipper.modules.system.domain.bo.SysOssBo
import com.zipper.modules.system.domain.entity.SysOssEntity
import com.zipper.modules.system.domain.vo.SysOssVo
import com.zipper.modules.system.mapper.SysOssMapper
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import com.zipper.framework.core.constant.CacheNames
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.service.OssService
import com.zipper.framework.core.utils.MapstructUtils.convert
import com.zipper.framework.core.utils.SpringUtilExt
import com.zipper.framework.core.utils.StringUtilsExt
import com.zipper.framework.core.utils.file.FileUtilsExt
import java.io.File
import java.io.IOException

/**
 * 文件上传 服务层实现
 *
 * @author Lion Li
 */
@Service
class SysOssServiceImpl(
    private val baseMapper: SysOssMapper
) : ISysOssService, OssService {


    override fun queryPageList(sysOss: SysOssBo, pageQuery: PageQuery): TableDataInfo<SysOssVo> {
        val lqw = buildQueryWrapper(sysOss)
        val result = baseMapper.selectVoPage<Page<SysOssVo>>(pageQuery.build(), lqw)
        val filterResult = result.records?.map { matchingUrl(it) }
        result.setRecords(filterResult)
        return TableDataInfo.build(result)
    }

    override fun listByIds(ossIds: Collection<Long>): List<SysOssVo> {
        val list = ArrayList<SysOssVo>()
        for (id in ossIds) {
            val vo: SysOssVo = SpringUtilExt.getAopProxy(this).getById(id)!!
            if (ObjectUtil.isNotNull(vo)) {
                try {
                    list.add(this.matchingUrl(vo))
                } catch (ignored: Exception) {
                    // 如果oss异常无法连接则将数据直接返回
                    list.add(vo)
                }
            }
        }
        return list
    }

    override fun selectUrlByIds(ossIds: String?): String? {
        val list: MutableList<String?> = ArrayList()
        for (id in StringUtilsExt.splitTo(ossIds, Convert::toLong)) {
            val vo: SysOssVo = SpringUtilExt.getAopProxy(this).getById(id)!!
            if (ObjectUtil.isNotNull(vo)) {
                try {
                    list.add(matchingUrl(vo).url)
                } catch (ignored: Exception) {
                    // 如果oss异常无法连接则将数据直接返回
                    list.add(vo.url)
                }
            }
        }
        return list.filterNotNull().joinToString(StringUtilsExt.SEPARATOR)
    }

    private fun buildQueryWrapper(bo: SysOssBo): KtQueryWrapper<SysOssEntity> {
        val params = bo.params
        val lqw = KtQueryWrapper(SysOssEntity::class.java)
        lqw.like(StringUtils.isNotBlank(bo.fileName), SysOssEntity::fileName, bo.fileName)
        lqw.like(StringUtils.isNotBlank(bo.originalName), SysOssEntity::originalName, bo.originalName)
        lqw.eq(StringUtils.isNotBlank(bo.fileSuffix), SysOssEntity::fileSuffix, bo.fileSuffix)
        lqw.eq(StringUtils.isNotBlank(bo.url), SysOssEntity::url, bo.url)
        lqw.between(
            params["beginCreateTime"] != null && params["endCreateTime"] != null,
            SysOssEntity::createTime, params["beginCreateTime"], params["endCreateTime"]
        )
        lqw.eq(ObjectUtil.isNotNull(bo.createBy), SysOssEntity::createBy, bo.createBy)
        lqw.eq(StringUtils.isNotBlank(bo.service), SysOssEntity::service, bo.service)
        lqw.orderByAsc(SysOssEntity::ossId)
        return lqw
    }

    @Cacheable(cacheNames = [CacheNames.SYS_OSS], key = "#ossId")
    override fun getById(ossId: Long?): SysOssVo? {
        return baseMapper.selectVoById(ossId)
    }

    @Throws(IOException::class)
    override fun download(ossId: Long?, response: HttpServletResponse) {
        val sysOss = SpringUtilExt.getAopProxy(this).getById(ossId)
        if (ObjectUtil.isNull(sysOss)) {
            throw ServiceException("文件数据不存在!")
        }
        FileUtilsExt.setAttachmentResponseHeader(response, sysOss!!.originalName)
        response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8"
        val storage: OssClient = OssFactory.instance(sysOss.service)
        try {
            storage.getObjectContent(sysOss.url).use { inputStream ->
                val available = inputStream.available()
                IoUtil.copy(inputStream, response.outputStream, available)
                response.setContentLength(available)
            }
        } catch (e: Exception) {
            throw ServiceException(e.message!!)
        }
    }

    override fun upload(file: MultipartFile): SysOssVo {
        val originalFilename = file.originalFilename ?: throw ServiceException("文件名称不能为空")
        val suffix = StringUtils.substring(originalFilename, originalFilename.lastIndexOf("."), originalFilename.length)
        val storage = OssFactory.instance()
        val uploadResult: UploadResult
        try {
            uploadResult = storage.uploadSuffix(file.bytes, suffix, file.contentType)
        } catch (e: IOException) {
            throw ServiceException(e.message!!)
        }
        // 保存文件信息
        return buildResultEntity(originalFilename, suffix, storage.configKey, uploadResult)
    }

    override fun upload(file: File): SysOssVo {
        val name = file.name
        val suffix = StringUtils.substring(name, name.lastIndexOf("."), name.length)
        val storage: OssClient = OssFactory.instance()
        val uploadResult = storage.uploadSuffix(file, suffix)
        // 保存文件信息
        return buildResultEntity(name, suffix, storage.configKey, uploadResult)
    }

    private fun buildResultEntity(originFilename: String, suffix: String, configKey: String, uploadResult: UploadResult): SysOssVo {
        val oss = SysOssEntity()
        oss.url = uploadResult.url
        oss.fileSuffix = suffix
        oss.fileName = uploadResult.filename
        oss.originalName = originFilename
        oss.service = configKey
        baseMapper.insert(oss)
        val sysOssVo = convert(oss, SysOssVo::class.java)
        return this.matchingUrl(sysOssVo)
    }

    override fun deleteWithValidByIds(ids: Collection<Long>, isValid: Boolean): Boolean {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        val list = baseMapper.selectBatchIds(ids)
        for (sysOss in list) {
            val storage = OssFactory.instance(sysOss.service)
            storage.delete(sysOss.url)
        }
        return baseMapper.deleteBatchIds(ids) > 0
    }

    /**
     * 匹配Url
     *
     * @param oss OSS对象
     * @return oss 匹配Url的OSS对象
     */
    private fun matchingUrl(oss: SysOssVo): SysOssVo {
        val storage: OssClient = OssFactory.instance(oss.service)
        // 仅修改桶类型为 private 的URL，临时URL时长为120s
        if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
            oss.url = storage.getPrivateUrl(oss.fileName, 120)
        }
        return oss
    }
}
