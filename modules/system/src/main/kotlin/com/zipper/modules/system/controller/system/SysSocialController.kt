package com.zipper.modules.system.controller.system

import com.zipper.framework.core.domain.R
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.vo.SysSocialVo
import com.zipper.modules.system.service.social.ISysSocialService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 社会化关系
 *
 * @author thiszhc
 * @date 2023-06-16
 */
@Validated
@RestController
@RequestMapping("/system/social")
class SysSocialController(
    private val socialUserService: ISysSocialService
) : BaseController() {
    /**
     * 查询社会化关系列表
     */
    @GetMapping("/list")
    fun list(): R<List<SysSocialVo>> {
        return R.ok<List<SysSocialVo>>(socialUserService.queryListByUserId(LoginHelper.getUserId()))
    }
}
