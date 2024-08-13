package com.zipper.framework.core.service

/**
 * 通用 用户服务
 *
 * @author Lion Li
 */
interface UserService {
    /**
     * 通过用户ID查询用户账户
     *
     * @param userId 用户ID
     * @return 用户账户
     */
    fun selectUserNameById(userId: Long): String?

    /**
     * 通过用户ID查询用户账户
     *
     * @param userId 用户ID
     * @return 用户账户
     */
    fun selectNicknameById(userId: Long): String?
}
