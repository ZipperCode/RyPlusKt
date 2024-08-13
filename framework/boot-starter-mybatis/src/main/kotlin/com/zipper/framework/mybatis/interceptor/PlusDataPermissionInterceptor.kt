package com.zipper.framework.mybatis.interceptor

import cn.hutool.core.collection.ConcurrentHashSet
import cn.hutool.core.util.ArrayUtil
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper
import com.baomidou.mybatisplus.core.toolkit.PluginUtils
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor
import com.zipper.framework.mybatis.annotation.DataColumn
import com.zipper.framework.mybatis.handler.PlusDataPermissionHandler
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.select.SelectBody
import net.sf.jsqlparser.statement.select.SetOperationList
import net.sf.jsqlparser.statement.update.Update
import org.apache.ibatis.executor.Executor
import org.apache.ibatis.executor.statement.StatementHandler
import org.apache.ibatis.mapping.BoundSql
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.mapping.SqlCommandType
import org.apache.ibatis.session.ResultHandler
import org.apache.ibatis.session.RowBounds
import java.sql.Connection
import java.sql.SQLException
import java.util.function.Consumer

/**
 * 数据权限拦截器
 *
 * @author Lion Li
 * @version 3.5.0
 */
class PlusDataPermissionInterceptor : JsqlParserSupport(), InnerInterceptor {
    private val dataPermissionHandler = PlusDataPermissionHandler()

    /**
     * 无效注解方法缓存用于快速返回
     */
    private val invalidCacheSet: MutableSet<String> = ConcurrentHashSet()

    @Throws(SQLException::class)
    override fun beforeQuery(
        executor: Executor,
        ms: MappedStatement,
        parameter: Any?,
        rowBounds: RowBounds,
        resultHandler: ResultHandler<*>?,
        boundSql: BoundSql
    ) {
        // 检查忽略注解
        if (InterceptorIgnoreHelper.willIgnoreDataPermission(ms.id)) {
            return
        }
        // 检查是否无效 无数据权限注解
        if (invalidCacheSet.contains(ms.id)) {
            return
        }
        val dataColumns = dataPermissionHandler.findAnnotation(ms.id)
        if (ArrayUtil.isEmpty(dataColumns)) {
            invalidCacheSet.add(ms.id)
            return
        }
        // 解析 sql 分配对应方法
        val mpBs = PluginUtils.mpBoundSql(boundSql)
        mpBs.sql(parserSingle(mpBs.sql(), ms.id))
    }

    override fun beforePrepare(sh: StatementHandler, connection: Connection, transactionTimeout: Int?) {
        val mpSh = PluginUtils.mpStatementHandler(sh)
        val ms = mpSh.mappedStatement()
        val sct = ms.sqlCommandType
        if (sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreDataPermission(ms.id)) {
                return
            }
            // 检查是否无效 无数据权限注解
            if (invalidCacheSet.contains(ms.id)) {
                return
            }
            val dataColumns = dataPermissionHandler.findAnnotation(ms.id)
            if (ArrayUtil.isEmpty(dataColumns)) {
                invalidCacheSet.add(ms.id)
                return
            }
            val mpBs = mpSh.mPBoundSql()
            mpBs.sql(parserMulti(mpBs.sql(), ms.id))
        }
    }

    override fun processSelect(select: Select, index: Int, sql: String, obj: Any?) {
        val selectBody = select.selectBody
        if (selectBody is PlainSelect) {
            this.setWhere(selectBody, obj as String)
        } else if (selectBody is SetOperationList) {
            selectBody.selects.forEach {
                setWhere(it as PlainSelect, obj as String)
            }
        }
    }

    override fun processUpdate(update: Update, index: Int, sql: String, obj: Any?) {
        val sqlSegment = dataPermissionHandler.getSqlSegment(update.where, (obj as String), false)
        update.where = sqlSegment
    }

    override fun processDelete(delete: Delete, index: Int, sql: String, obj: Any?) {
        val sqlSegment = dataPermissionHandler.getSqlSegment(delete.where, (obj as String?), false)
        delete.where = sqlSegment
    }

    /**
     * 设置 where 条件
     *
     * @param plainSelect       查询对象
     * @param mappedStatementId 执行方法id
     */
    protected fun setWhere(plainSelect: PlainSelect, mappedStatementId: String?) {
        val sqlSegment = dataPermissionHandler.getSqlSegment(plainSelect.where, mappedStatementId!!, true)
        plainSelect.where = sqlSegment
    }
}

