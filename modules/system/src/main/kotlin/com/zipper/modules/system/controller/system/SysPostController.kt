package com.zipper.modules.system.controller.system

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.mybatis.core.page.PageQuery
import com.zipper.framework.mybatis.core.page.TableDataInfo
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysPostBo
import com.zipper.modules.system.domain.vo.SysPostVo
import com.zipper.modules.system.service.post.ISysPostService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import com.zipper.framework.core.constant.UserConstants
import org.zipper.framework.excel.utils.ExcelUtil.exportExcel

/**
 * 岗位信息操作处理
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/system/post")
class SysPostController(private val postService: ISysPostService) : BaseController() {
    /**
     * 获取岗位列表
     */
    @SaCheckPermission("system:post:list")
    @GetMapping("/list")
    fun list(post: SysPostBo, pageQuery: PageQuery): TableDataInfo<SysPostVo> {
        return postService.selectPagePostList(post, pageQuery)
    }

    /**
     * 导出岗位列表
     */
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:post:export")
    @PostMapping("/export")
    fun export(post: SysPostBo, response: HttpServletResponse) {
        val list = postService.selectPostList(post)
        exportExcel(list, "岗位数据", SysPostVo::class.java, response)
    }

    /**
     * 根据岗位编号获取详细信息
     *
     * @param postId 岗位ID
     */
    @SaCheckPermission("system:post:query")
    @GetMapping(value = ["/{postId}"])
    fun getInfo(@PathVariable postId: Long?): R<SysPostVo?> {
        return R.ok(postService.selectPostById(postId))
    }

    /**
     * 新增岗位
     */
    @SaCheckPermission("system:post:add")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    fun add(@Validated @RequestBody post: SysPostBo): R<Void> {
        if (!postService.checkPostNameUnique(post)) {
            return R.fail("新增岗位'" + post.postName + "'失败，岗位名称已存在")
        } else if (!postService.checkPostCodeUnique(post)) {
            return R.fail("新增岗位'" + post.postName + "'失败，岗位编码已存在")
        }
        return toAjax(postService.insertPost(post))
    }

    /**
     * 修改岗位
     */
    @SaCheckPermission("system:post:edit")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    fun edit(@Validated @RequestBody post: SysPostBo): R<Void> {
        if (!postService.checkPostNameUnique(post)) {
            return R.fail("修改岗位'" + post.postName + "'失败，岗位名称已存在")
        } else if (!postService.checkPostCodeUnique(post)) {
            return R.fail("修改岗位'" + post.postName + "'失败，岗位编码已存在")
        } else if (UserConstants.POST_DISABLE == post.status && postService.countUserPostById(post.postId) > 0) {
            return R.fail("该岗位下存在已分配用户，不能禁用!")
        }
        return toAjax(postService.updatePost(post))
    }

    /**
     * 删除岗位
     *
     * @param postIds 岗位ID串
     */
    @SaCheckPermission("system:post:remove")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    fun remove(@PathVariable postIds: Array<Long>): R<Void> {
        return toAjax(postService.deletePostByIds(postIds))
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    fun optionselect(): R<List<SysPostVo>> {
        val postBo = SysPostBo()
        postBo.status = UserConstants.POST_NORMAL
        val posts = postService.selectPostList(postBo)
        return R.ok(posts)
    }
}
