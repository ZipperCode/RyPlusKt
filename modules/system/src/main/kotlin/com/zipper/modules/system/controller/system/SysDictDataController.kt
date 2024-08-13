package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.hutool.core.util.ObjectUtil
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysDictDataBo
import com.zipper.modules.system.domain.vo.SysDictDataVo
import com.zipper.modules.system.service.dict.ISysDictDataService
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
@RequestMapping("/system/dict/data")
class SysDictDataController(
    private val dictDataService: ISysDictDataService,
    private val dictTypeService: ISysDictTypeService
) :
    BaseController() {
    /**
     * 查询字典数据列表
     */
    @SaCheckPermission("system:dict:list")
    @GetMapping("/list")
    fun list(dictData: SysDictDataBo, pageQuery: PageQuery): TableDataInfo<SysDictDataVo> {
        return dictDataService.selectPageDictDataList(dictData, pageQuery)
    }

    /**
     * 导出字典数据列表
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:dict:export")
    @PostMapping("/export")
    fun export(dictData: SysDictDataBo?, response: HttpServletResponse) {
        val list = dictDataService.selectDictDataList(dictData!!)
        exportExcel(list, "字典数据", SysDictDataVo::class.java, response)
    }

    /**
     * 查询字典数据详细
     *
     * @param dictCode 字典code
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping(value = ["/{dictCode}"])
    fun getInfo(@PathVariable dictCode: Long?): R<SysDictDataVo?> {
        return R.ok(dictDataService.selectDictDataById(dictCode))
    }

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType 字典类型
     */
    @GetMapping(value = ["/type/{dictType}"])
    fun dictType(@PathVariable dictType: String?): R<List<SysDictDataVo>?> {
        var data = dictTypeService.selectDictDataByType(dictType)
        if (ObjectUtil.isNull(data)) {
            data = ArrayList()
        }
        return R.ok(data)
    }

    /**
     * 新增字典类型
     */
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    fun add(@Validated @RequestBody dict: SysDictDataBo?): R<Void> {
        dictDataService.insertDictData(dict!!)
        return R.ok()
    }

    /**
     * 修改保存字典类型
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    fun edit(@Validated @RequestBody dict: SysDictDataBo?): R<Void> {
        dictDataService.updateDictData(dict!!)
        return R.ok()
    }

    /**
     * 删除字典类型
     *
     * @param dictCodes 字典code串
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    fun remove(@PathVariable dictCodes: Array<Long>): R<Void> {
        dictDataService.deleteDictDataByIds(dictCodes)
        return R.ok()
    }
}
