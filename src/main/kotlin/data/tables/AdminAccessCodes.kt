package com.rabindradev.data.tables

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

object AdminAccessCodesTable : Table("admin_access_codes") {
    val id = uuid("id").default(UUID.randomUUID())
    val accessCode = uuid("access_code").uniqueIndex()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val expiresAt = datetime("expires_at")
    val valid = bool("valid").default(true)

    override val primaryKey = PrimaryKey(id)
}

fun AdminAccessCodesTable.insertAccessCode(expiryMinutes: Long = 60): String {
    val generatedCode = UUID.randomUUID().toString()
    transaction {
        insert {
            it[id] = UUID.randomUUID()
            it[accessCode] = UUID.fromString(generatedCode)
            it[createdAt] = LocalDateTime.now()
            it[expiresAt] = LocalDateTime.now().plusMinutes(expiryMinutes)
            it[valid] = true
        }
    }
    return generatedCode
}

fun AdminAccessCodesTable.validateAccessCode(code: String): Boolean {
    return transaction {
        val uuidCode = runCatching { UUID.fromString(code) }.getOrNull() ?: return@transaction false

        select { (accessCode eq uuidCode) and (valid eq true) and (expiresAt greaterEq LocalDateTime.now()) }.singleOrNull() != null
    }
}

fun AdminAccessCodesTable.invalidateAccessCode(code: String) {
    transaction {
        val uuidCode = runCatching { UUID.fromString(code) }.getOrNull() ?: return@transaction
        update({ accessCode eq uuidCode }) {
            it[valid] = false
        }
    }
}