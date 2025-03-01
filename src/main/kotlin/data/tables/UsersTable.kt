package com.rabindradev.data.tables

import org.jetbrains.exposed.sql.Table
import java.util.UUID

object UsersTable : Table("users") {
    val id = uuid("id").default(UUID.randomUUID())
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}
