package com.rabindradev.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

object ErrorLogs : Table("error_logs") {
    val id = uuid("id").default(UUID.randomUUID())
    val message = text("message")
    val stackTrace = text("stack_trace").nullable()
    val timestamp = datetime("timestamp").default(LocalDateTime.now())
    val endpoint = varchar("endpoint", 255).nullable()
    val userId = varchar("user_id", 512).nullable()

    override val primaryKey = PrimaryKey(id)
}

fun ErrorLogs.insertErrorLog(
    message: String, stackTrace: String? = null, endpoint: String? = null, userId: String? = null
): Result<Unit> {
    return try {
        transaction {
            insert {
                it[id] = UUID.randomUUID()
                it[ErrorLogs.message] = message
                it[ErrorLogs.stackTrace] = stackTrace
                it[ErrorLogs.timestamp] = LocalDateTime.now()
                it[ErrorLogs.endpoint] = endpoint
                it[ErrorLogs.userId] = userId
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
