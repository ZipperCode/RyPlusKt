package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysDictTypeBo
import com.zipper.modules.system.domain.vo.SysDictTypeVo
import com.zipper.modules.system.service.dict.ISysDictTypeService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.zipper.framework.excel.utils.ExcelUtil.exportExcel

/**
 * 数据字典信息
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/system/dict/type")
class SysDictTypeController(private val dictTypeService: ISysDictTypeService) : BaseController() {
    /**
     * 查询字典类型列表
     */
    @SaCheckPermission("system:dict:list")
    @GetMapping("/list")
    fun list(dictType: SysDictTypeBo, pageQuery: PageQuery): TableDataInfo<SysDictTypeVo> {
        return dictTypeService.selectPageDictTypeList(dictType, pageQuery)
    }

    /**
     * 导出字典类型列表
     */
    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:dict:export")
    @PostMapping("/export")
    fun export(dictType: SysDictTypeBo, response: HttpServletResponse) {
        val list = dictTypeService.selectDictTypeList(dictType)
        exportExcel(list, "字典类型", SysDictTypeVo::class.java, response)
    }

    /**
     * 查询字典类型详细
     *
     * @param dictId 字典ID
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping(value = ["/{dictId}"])
    fun getInfo(@PathVariable dictId: Long?): R<SysDictTypeVo?> {
        return R.ok(dictTypeService.selectDictTypeById(dictId))
    }

    /**
     * 新增字典类型
     */
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    fun add(@Validated @RequestBody dict: SysDictTypeBo): R<Void> {
        if (!dictTypeService.checkDictTypeUnique(dict)) {
            return R.fail("新增字典'" + dict.dictName + "'失败，字典类型已存在")
        }
        dictTypeService.insertDictType(dict)
        return R.ok()
    }

    /**
     * 修改字典类型
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    fun edit(@Validated @RequestBody dict: SysDictTypeBo): R<Void> {
        if (!dictTypeService.checkDictTypeUnique(dict)) {
            return R.fail("修改字典'" + dict.dictName + "'失败，字典类型已存在")
        }
        dictTypeService.updateDictType(dict)
        return R.ok()
    }

    /**
     * 删除字典类型
     *
     * @param dictIds 字典ID串
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    fun remove(@PathVariable dictIds: Array<Long>): R<Void> {
        dictTypeService.deleteDictTypeByIds(dictIds)
        return R.ok()
    }

    /**
     * 刷新字典缓存
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    fun refreshCache(): R<Void> {
        dictTypeService.resetDictCache()
        return R.ok()
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    fun optionselect(): R<List<SysDictTypeVo>> {
        val dictTypes = dictTypeService.selectDictTypeAll()
        return R.ok(dictTypes)
    }
}
