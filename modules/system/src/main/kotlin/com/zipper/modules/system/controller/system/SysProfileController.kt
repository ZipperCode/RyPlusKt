package com.zipper.modules.system.controller.system

import cn.dev33.satoken.secure.BCrypt
import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.io.FileUtil
import com.zipper.framework.core.domain.R
import com.zipper.framework.log.annotation.Log
import com.zipper.framework.log.enums.BusinessType
import com.zipper.framework.satoken.utils.LoginHelper
import com.zipper.framework.web.core.BaseController
import com.zipper.modules.system.domain.bo.SysUserBo
import com.zipper.modules.system.domain.bo.SysUserPasswordBo
import com.zipper.modules.system.domain.bo.SysUserProfileBo
import com.zipper.modules.system.domain.vo.AvatarVo
import com.zipper.modules.system.domain.vo.ProfileVo
import com.zipper.modules.system.service.oss.ISysOssService
import com.zipper.modules.system.service.user.ISysUserService
import org.apache.commons.lang3.StringUtils
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import com.zipper.framework.core.utils.file.MimeTypeUtils
import com.zipper.framework.encrypt.annotation.ApiEncrypt
import java.util.*

/**
 * 个人信息 业务处理
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/system/user/profile")
class SysProfileController(
    private val userService: ISysUserService,
    private val ossService: ISysOssService
) : BaseController() {
    /**
     * 个人信息
     */
    @GetMapping
    fun profile(): R<ProfileVo> {
        val user = userService.selectUserById(LoginHelper.getUserId())
        val profileVo = ProfileVo()
        profileVo.user = user
        profileVo.roleGroup = userService.selectUserRoleGroup(user!!.userName)
        profileVo.postGroup = userService.selectUserPostGroup(user.userName)
        return R.ok(profileVo)
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    fun updateProfile(@RequestBody profile: SysUserProfileBo?): R<Void> {
        val user = BeanUtil.toBean(profile, SysUserBo::class.java)
        val username = LoginHelper.getUserName()
        if (StringUtils.isNotEmpty(user.phonenumber) && !userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'$username'失败，手机号码已存在")
        }
        if (StringUtils.isNotEmpty(user.email) && !userService.checkEmailUnique(user)) {
            return R.fail("修改用户'$username'失败，邮箱账号已存在")
        }
        user.userId = LoginHelper.getUserId()
        if (userService.updateUserProfile(user) > 0) {
            return R.ok()
        }
        return R.fail("修改个人信息异常，请联系管理员")
    }

    /**
     * 重置密码
     *
     * @param bo 新旧密码
     */
    @com.zipper.framework.encrypt.annotation.ApiEncrypt
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    fun updatePwd(@Validated @RequestBody bo: SysUserPasswordBo): R<Void> {
        val user = userService.selectUserById(LoginHelper.getUserId())
        val password = user!!.password
        if (!BCrypt.checkpw(bo.oldPassword, password)) {
            return R.fail("修改密码失败，旧密码错误")
        }
        if (BCrypt.checkpw(bo.newPassword, password)) {
            return R.fail("新密码不能与旧密码相同")
        }

        if (userService.resetUserPwd(user.userId, BCrypt.hashpw(bo.newPassword)) > 0) {
            return R.ok()
        }
        return R.fail("修改密码异常，请联系管理员")
    }

    /**
     * 头像上传
     *
     * @param avatarfile 用户头像
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping(value = ["/avatar"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun avatar(@RequestPart("avatarfile") avatarfile: MultipartFile): R<AvatarVo> {
        if (!avatarfile.isEmpty) {
            val extension = FileUtil.extName(avatarfile.originalFilename)
            if (!StringUtils.equalsAnyIgnoreCase(extension, *MimeTypeUtils.IMAGE_EXTENSION)) {
                return R.fail<AvatarVo>("文件格式不正确，请上传" + MimeTypeUtils.IMAGE_EXTENSION.contentToString() + "格式")
            }
            val oss = ossService.upload(avatarfile)
            val avatar = oss.url
            if (userService.updateUserAvatar(LoginHelper.getUserId(), oss.ossId)) {
                val avatarVo = AvatarVo()
                avatarVo.imgUrl = avatar
                return R.ok(avatarVo)
            }
        }
        return R.fail("上传图片异常，请联系管理员")
    }
}
