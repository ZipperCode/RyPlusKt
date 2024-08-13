package com.zipper.framework.mybatis.handler

import cn.hutool.core.annotation.AnnotationUtil
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.extra.spring.SpringUtil
import com.zipper.framework.core.domain.model.LoginUser
import com.zipper.framework.mybatis.annotation.DataColumn
import com.zipper.framework.mybatis.annotation.DataPermission
import com.zipper.framework.mybatis.enums.DataScopeType
import com.zipper.framework.mybatis.helper.DataPermissionHelper
import com.zipper.framework.satoken.utils.LoginHelper
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.expression.Parenthesis
import net.sf.jsqlparser.expression.operators.conditional.AndExpression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import org.apache.commons.lang3.StringUtils
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.expression.BeanResolver
import org.springframework.expression.ExpressionParser
import org.springframework.expression.ParserContext
import org.springframework.expression.common.TemplateParserContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import com.zipper.framework.core.exception.ServiceException
import com.zipper.framework.core.utils.StreamUtils
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import net.sf.jsqlparser.JSQLParserException as JSQLParserException1

/**
 * 数据权限过滤
 *
 * @author Lion Li
 * @version 3.5.0
 */
class PlusDataPermissionHandler {
    /**
     * 方法或类(名称) 与 注解的映射关系缓存
     */
    private val dataPermissionCacheMap: MutableMap<String, DataPermission> = ConcurrentHashMap<String, DataPermission>()

    /**
     * spel 解析器
     */
    private val parser: ExpressionParser = SpelExpressionParser()
    private val parserContext: ParserContext = TemplateParserContext()

    /**
     * bean解析器 用于处理 spel 表达式中对 bean 的调用
     */
    private val beanResolver: BeanResolver = BeanFactoryResolver(SpringUtil.getBeanFactory())


    fun getSqlSegment(where: Expression, mappedStatementId: String?, isSelect: Boolean): Expression {
        val dataColumns = findAnnotation(mappedStatementId)
        var currentUser = DataPermissionHelper.getVariable<LoginUser>("user")
        if (ObjectUtil.isNull(currentUser)) {
            currentUser = LoginHelper.getLoginUser()
            DataPermissionHelper.setVariable("user", currentUser)
        }
        // 如果是超级管理员或租户管理员，则不过滤数据
        if (LoginHelper.isSuperAdmin() || LoginHelper.isTenantAdmin()) {
            return where
        }
        val dataFilterSql = buildDataFilter(dataColumns, isSelect)
        if (dataFilterSql.isEmpty()) {
            return where
        }
        try {
            val expression: Expression = CCJSqlParserUtil.parseExpression(dataFilterSql)
            // 数据权限使用单独的括号 防止与其他条件冲突
            val parenthesis = Parenthesis(expression)
            return if (ObjectUtil.isNotNull(where)) {
                AndExpression(where, parenthesis)
            } else {
                parenthesis
            }
        } catch (e: JSQLParserException1) {
            throw ServiceException("数据权限解析异常 => " + e.message)
        }
    }

    /**
     * 构造数据过滤sql
     */
    private fun buildDataFilter(dataColumns: Array<out DataColumn>?, isSelect: Boolean): String {
        dataColumns ?: return ""
        // 更新或删除需满足所有条件
        val joinStr = if (isSelect) " OR " else " AND "
        val user: LoginUser = DataPermissionHelper.getVariable("user") ?: throw ServiceException("获取用户信息异常")
        val context = StandardEvaluationContext()
        context.beanResolver = beanResolver
//        DataPermissionHelper.getContext().forEach(context::setVariable)
        val conditions: MutableSet<String?> = HashSet()
        for (role in user.roles) {
            user.roleId = role.roleId
            // 获取角色权限泛型
            val type = DataScopeType.findCode(role.dataScope)
            if (ObjectUtil.isNull(type)) {
                throw ServiceException("角色数据范围异常 => " + role.dataScope)
            }
            // 全部数据权限直接返回
            if (type!! == DataScopeType.ALL) {
                return ""
            }
            var isSuccess = false
            for (dataColumn in dataColumns) {
                if (dataColumn.key.size != dataColumn.value.size) {
                    throw ServiceException("角色数据范围异常 => key与value长度不匹配")
                }

                // 不包含 key 变量 则不处理
                if (!StringUtils.containsAny(type.sqlTemplate, *dataColumn.key.map { key -> "#$key" }.toTypedArray())) {
                    continue
                }
                // 设置注解变量 key 为表达式变量 value 为变量值
                for (i in 0 until dataColumn.key.size) {
                    context.setVariable(dataColumn.key[i], dataColumn.value[i])
                }

                // 解析sql模板并填充
                val sql = parser.parseExpression(type.sqlTemplate, parserContext).getValue(context, String::class.java)
                conditions.add(joinStr + sql)
                isSuccess = true
            }
            // 未处理成功则填充兜底方案
            if (!isSuccess && StringUtils.isNotBlank(type.elseSql)) {
                conditions.add(joinStr + type.elseSql)
            }
        }

        if (CollUtil.isNotEmpty(conditions)) {
            return conditions.joinToString().substring(joinStr.length)
        }
        return ""
    }

    fun findAnnotation(mappedStatementId: String?): Array<out DataColumn>? {
        if (mappedStatementId.isNullOrEmpty()) {
            return emptyArray()
        }
        val sb = StringBuilder(mappedStatementId)
        val index = sb.lastIndexOf(".")
        val clazzName = sb.substring(0, index)
        val methodName = sb.substring(index + 1, sb.length)
        val clazz: Class<*>
        try {
            clazz = ClassUtil.loadClass<Any>(clazzName)
        } catch (e: Exception) {
            return null
        }
        val methods = Arrays.stream(ClassUtil.getDeclaredMethods(clazz))
            .filter { method: Method -> method.name == methodName }.toList()
        var dataPermission: DataPermission?
        // 获取方法注解
        for (method in methods) {
            dataPermission = dataPermissionCacheMap[mappedStatementId]
            if (ObjectUtil.isNotNull(dataPermission)) {
                return dataPermission!!.value
            }
            if (AnnotationUtil.hasAnnotation(method, DataPermission::class.java)) {
                dataPermission = AnnotationUtil.getAnnotation(method, DataPermission::class.java)
                dataPermissionCacheMap[mappedStatementId] = dataPermission
                return dataPermission.value
            }
        }
        dataPermission = dataPermissionCacheMap[clazz.name]
        if (ObjectUtil.isNotNull(dataPermission)) {
            return dataPermission!!.value
        }
        // 获取类注解
        if (AnnotationUtil.hasAnnotation(clazz, DataPermission::class.java)) {
            dataPermission = AnnotationUtil.getAnnotation(clazz, DataPermission::class.java)
            dataPermissionCacheMap[clazz.name] = dataPermission
            return dataPermission.value
        }
        return null
    }


}
