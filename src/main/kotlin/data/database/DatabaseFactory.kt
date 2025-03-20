package com.rabindradev.data.database

import com.rabindradev.data.tables.AdminAccessCodesTable
import com.rabindradev.data.tables.AdminsTable
import com.rabindradev.data.tables.ErrorLogs
import com.rabindradev.data.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource

    fun init(config: ApplicationConfig) {
        val dbConfig = config.config("ktor.database")
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbConfig.property("url").getString()
            driverClassName = dbConfig.property("driver").getString()
            username = dbConfig.property("user").getString()
            password = dbConfig.property("password").getString()
            maximumPoolSize = dbConfig.property("maxPoolSize").getString().toInt()
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(UsersTable)
            SchemaUtils.create(AdminAccessCodesTable)
            SchemaUtils.create(AdminsTable)
            SchemaUtils.create(ErrorLogs)
        }
    }

    fun getDataSource(): HikariDataSource = dataSource
}