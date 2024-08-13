package com.zipper.framework.mybatis.enums

import lombok.AllArgsConstructor
import lombok.Getter

/**
 * 数据库类型
 *
 * @author Lion Li
 */
enum class DataBaseType(
    val type: String
) {
    /**
     * MySQL
     */
    MY_SQL("MySQL"),

    /**
     * Oracle
     */
    ORACLE("Oracle"),

    /**
     * PostgreSQL
     */
    POSTGRE_SQL("PostgreSQL"),

    /**
     * SQL Server
     */
    SQL_SERVER("Microsoft SQL Server");

    fun isMysql() = this == MY_SQL
    fun isOracle() = this == ORACLE
    fun isPostgreSql() = this == POSTGRE_SQL
    fun isSqlServer() = this == SQL_SERVER

    companion object {

        fun find(databaseProductName: String): DataBaseType? {
            return entries.firstOrNull { it.type == databaseProductName }
        }
    }
}
