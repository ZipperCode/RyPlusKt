package com.zipper.framework.mybatis.helper

import cn.hutool.core.convert.Convert
import cn.hutool.extra.spring.SpringUtil
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource
import com.zipper.framework.mybatis.enums.DataBaseType
import lombok.AccessLevel
import lombok.NoArgsConstructor
import com.zipper.framework.core.exception.ServiceException
import java.sql.SQLException

/**
 * 数据库助手
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
object DataBaseHelper {
    private val DS: DynamicRoutingDataSource by lazy {
        SpringUtil.getBean(DynamicRoutingDataSource::class.java)
    }

    /**
     * 获取当前数据库类型
     */
    fun getDataBaseType(): DataBaseType? {
        val dataSource = DS.determineDataSource()
        try {
            dataSource.connection.use { conn ->
                val metaData = conn.metaData
                val databaseProductName = metaData.databaseProductName
                return DataBaseType.find(databaseProductName)
            }
        } catch (e: SQLException) {
            throw ServiceException(e.message ?: "")
        }
    }

    fun findInSet(var1: Any?, var2: String?): String {
        val dataBaseType = getDataBaseType()
        val var1Str = Convert.toStr(var1)
        return when (dataBaseType) {
            DataBaseType.SQL_SERVER -> {
                // charindex(',100,' , ',0,100,101,') <>
                String.format("charindex(',%s,' , ','+%s+',') <> 0", var1Str, var2)
            }

            DataBaseType.POSTGRE_SQL -> {
                // (select position(',100,' in ',0,100,101,')) <> 0
                String.format("position(',%s,' in ','||%s||',')) <> 0", var1Str, var2)
            }

            DataBaseType.ORACLE -> {
                // instr(',0,100,101,' , ',100,') <> 0
                String.format("instr(',%s,' , ',%s,') <> 0", var2, var1Str)
            }
            // find_in_set(100 , '0,100,101')
            else -> String.format("find_in_set('%s' , %s) <> 0", var1Str, var2)
        }
    }

    /**
     * 获取当前加载的数据库名
     */
    fun getDataSourceNameList(): List<String> {
        return ArrayList(DS.dataSources.keys)
    }
}
