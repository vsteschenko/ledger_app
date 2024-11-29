package com.example.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime




object TransactionTable:Table() {
    val id = integer("id").autoIncrement()
    val userEmail = varchar("userEmail",512).references(UserTable.email)
    val comment = text("comment")
    val category = varchar("category",512)
    val location = varchar("location",512)
    val amount = decimal("amount", precision = 10, scale = 2)
    val date = datetime("date")

    override val primaryKey: PrimaryKey = PrimaryKey(id)

}