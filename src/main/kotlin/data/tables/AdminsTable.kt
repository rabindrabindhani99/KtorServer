package com.rabindradev.data.tables

import com.rabindradev.domain.models.AdminResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.ZoneId
import java.util.*

object AdminsTable : Table("admins") {
    val id = uuid("id").default(UUID.randomUUID())
    val token = varchar("token", 512).default("")
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val name = varchar("name", 255)
    val phone = varchar("phone", 20).nullable()
    val role = varchar("role", 50).default("admin")
    val status = varchar("status", 20).default("active")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    val lastSeen = datetime("last_seen").nullable()

    override val primaryKey = PrimaryKey(id)
}

fun AdminsTable.insertAdmin(
    email: String, hashedPassword: String, name: String, phone: String?, token: String
): AdminResponse? {
    return transaction {
        val adminId = UUID.randomUUID()
        val now = Clock.System.now().toJavaLocalDateTime()

        val insertedRow = insert {
            it[id] = adminId
            it[AdminsTable.token] = token
            it[AdminsTable.email] = email
            it[AdminsTable.password] = hashedPassword
            it[AdminsTable.name] = name
            it[AdminsTable.phone] = phone
            it[createdAt] = now
            it[updatedAt] = now
            it[lastSeen] = now
        }.resultedValues?.firstOrNull()

        insertedRow?.toAdminResponse()
    }
}

fun AdminsTable.updateLastSeen(email: String) {
    transaction {
        update({ AdminsTable.email eq email }) {
            it[lastSeen] = Clock.System.now().toJavaLocalDateTime()
        }
    }
}

fun AdminsTable.getAdminByEmail(email: String): AdminResponse? {
    return transaction {
        select { AdminsTable.email eq email }.mapNotNull { it.toAdminResponse() }.singleOrNull()
    }
}

fun AdminsTable.deleteAdmin(email: String): Boolean {
    return transaction {
        deleteWhere { AdminsTable.email eq email } > 0
    }
}

fun AdminsTable.adminExists(email: String): Boolean {
    return transaction {
        select { AdminsTable.email eq email }.count() > 0
    }
}

private fun ResultRow.toAdminResponse(): AdminResponse {
    return AdminResponse(
        id = this[AdminsTable.id].toString(),
        token = this[AdminsTable.token],
        email = this[AdminsTable.email],
        name = this[AdminsTable.name],
        phone = this[AdminsTable.phone],
        role = this[AdminsTable.role],
        status = this[AdminsTable.status],
        createdAt = this[AdminsTable.createdAt].toKotlinLocalDateTime(),
        updatedAt = this[AdminsTable.updatedAt].toKotlinLocalDateTime(),
        lastSeen = this[AdminsTable.lastSeen]?.toKotlinLocalDateTime()
    )
}

fun AdminsTable.getAllAdmins(): List<AdminResponse> {
    return transaction {
        selectAll().map { it.toAdminResponse() }
    }
}

fun Instant.toJavaLocalDateTime(): java.time.LocalDateTime {
    return this.toJavaInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
}