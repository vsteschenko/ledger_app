package com.example.repository

import com.example.data.model.Transaction
import com.example.data.model.User
import com.example.data.table.TransactionTable
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime


class Repo {
    suspend fun addUser(user:User){
        dbQuery {
            UserTable.insert { ut ->
                ut[UserTable.email] = user.email
                ut[UserTable.hashPassword] = user.hashPassword
                ut[UserTable.name] = user.userName
            }
        }
    }

    suspend fun findUserByEmail(email: String) = dbQuery {
        UserTable
            .selectAll()
            .mapNotNull { rowToUser(it) }
            .find { it.email == email }
    }

    private fun rowToUser(row:ResultRow?):User?{
        if(row == null){
            return null
        }
        return User(
            email = row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            userName = row[UserTable.name]
        )
    }

    // TRANSACTIONS

    suspend fun addTransaction(transaction:Transaction, email:String, currentTime: LocalDateTime){
        dbQuery {

            TransactionTable.insert {tt ->
                tt[TransactionTable.userEmail] = email
                tt[TransactionTable.comment] = transaction.comment
                tt[TransactionTable.category] = transaction.category
                tt[TransactionTable.date] = currentTime
                tt[TransactionTable.amount] = transaction.amount
                tt[TransactionTable.location] = transaction.location
            }
        }
    }

    suspend fun getAllTransactions(email:String):List<Transaction> = dbQuery {
        TransactionTable.select {
            TransactionTable.userEmail.eq(email)
        }.mapNotNull { rowToTransaction(it) }
    }

    suspend fun updateTransaction(transaction:Transaction,email:String){

        dbQuery{
            TransactionTable.update(
                where = {
                    TransactionTable.userEmail.eq(email) and TransactionTable.id.eq(transaction.id)
                }
            ){ tt->
                tt[TransactionTable.comment] = transaction.comment
                tt[TransactionTable.amount] = transaction.amount
                tt[TransactionTable.category] = transaction.category
            }
        }

    }

    suspend fun deleteTransaction(id:Int,email:String) {
        dbQuery {
            TransactionTable.deleteWhere { TransactionTable.userEmail.eq(email) and TransactionTable.id.eq(id) }
        }
    }

    private fun rowToTransaction(row:ResultRow?): Transaction? {
        if(row == null){
            return null
        }
        return Transaction(
            id = row[TransactionTable.id],
            comment = row[TransactionTable.comment],
            category = row[TransactionTable.category],
            date = row[TransactionTable.date].toString(),
            amount = row[TransactionTable.amount],
            location = row[TransactionTable.location],
            userEmail = row[TransactionTable.userEmail]
        )
    }
}