package com.zipper.modules.system.mapper

import com.zipper.framework.mybatis.core.mapper.BaseMapperPlus
import com.zipper.framework.mybatis.core.mapper.BaseMapperPlusVo
import com.zipper.modules.system.domain.entity.SysPostEntity
import com.zipper.modules.system.domain.vo.SysPostVo

/**
 * 岗位信息 数据层
 *
 * @author Lion Li
 */
interface SysPostMapper : BaseMapperPlusVo<SysPostEntity, SysPostVo> {
    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    fun selectPostListByUserId(userId: Long?): List<Long>

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    fun selectPostsByUserName(userName: String?): List<SysPostVo>
}
