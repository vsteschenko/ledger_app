package com.example.routes

import com.example.data.model.SimpleResponse
import com.example.data.model.Transaction
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.locations.*
import io.ktor.server.routing.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val TXS = "$API_VERSION/txs"
const val CREATE_TXS = "$TXS/create"
const val UPDATE_TXS = "$TXS/update"
const val DELETE_TXS = "$TXS/delete"
const val SUM_TXS = "$TXS/sum"
const val CATEGORY_TXS = "$TXS/category"
const val CATEGORY_TXS_SUM = "$TXS/category/sum"
const val FILTER_BY_DATE = "$TXS/filterByDate"
const val TEST = "protected"
const val CREATE_TXS_WITH_TIME = "$TXS/create_with_time"

@Location(CREATE_TXS)
class TransactionCreateRoute

@Location(CREATE_TXS_WITH_TIME)
class TransactionCreateWithTimeRoute

@Location(UPDATE_TXS)
class TransactionUpdateRoute

@Location(DELETE_TXS)
class TransactionDeleteRoute

@Location(TXS)
class TransactionGetRoute

@Location(FILTER_BY_DATE)
class TransactionFilterByDateRoute

@Location(SUM_TXS)
class TransactionGetSumRoute

@Location(CATEGORY_TXS)
class TransactionGetCategoryRoute

@Location(CATEGORY_TXS_SUM)
class TransactionGetCategorySumRoute

@Location(TEST)
class TransactionGetTestRoute

data class ApiResponse(val message: String, val status: Int)

fun Route.TransactionRoutes(
    db: Repo,
    hashFunction: (String)->String
) {
    authenticate("jwt") {
        post<TransactionCreateRoute>{
            val transaction = try {
                call.receive<Transaction>()
            } catch (e:Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                val currentTime = LocalDateTime.now()
                db.addTransaction(transaction, email, currentTime)
                call.respond(HttpStatusCode.OK,SimpleResponse(true, "TX Added Successfully!"))
            } catch(e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message ?: "Some Problem"))
            }
        }
        post<TransactionCreateWithTimeRoute>{
            val transaction = try {
                call.receive<Transaction>()
            } catch (e:Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                val currentTime = LocalDateTime.now()
                val currentTimestamp = Timestamp.valueOf(currentTime)
//                val transactionWithTime = if (transaction.date == null) {
//                    transaction.copy(date = currentTimestamp)
//                } else {
//                    transaction
//                }
                db.addTransaction(transaction, email, currentTimestamp)
                call.respond(HttpStatusCode.OK,SimpleResponse(true, "TX Added Successfully!"))
            } catch(e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message ?: "Some Problem"))
            }
        }

        get<TransactionGetRoute> {
            try {
                val email = call.principal<User>()!!.email
                val transactions = db.getAllTransactions(email)
                call.respond(HttpStatusCode.OK,transactions)
                println(transactions[0].date)
            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict, emptyList<Transaction>())
            }
        }
        post<TransactionUpdateRoute>{
            val transaction = try {
                call.receive<Transaction>()
            } catch (e:Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }
            try {
                val email = call.principal<User>()!!.email
                db.updateTransaction(transaction, email)
                call.respond(HttpStatusCode.OK,SimpleResponse(true, "TX Updated Successfully!"))
            } catch(e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message ?: "Some Problem"))
            }
        }
        get<TransactionFilterByDateRoute> {
            try {
                val email = call.principal<User>()!!.email
                val monthName = call.request.queryParameters["month"]?.lowercase()
                val year = call.request.queryParameters["year"]?.toIntOrNull()

                if (monthName == null || year == null) {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Invalid or missing query parameters: month and year"))
                    return@get
                }

                val monthNumber = when (monthName) {
                    "january" -> 1
                    "february" -> 2
                    "march" -> 3
                    "april" -> 4
                    "may" -> 5
                    "june" -> 6
                    "july" -> 7
                    "august" -> 8
                    "september" -> 9
                    "october" -> 10
                    "november" -> 11
                    "december" -> 12
                    else -> null
                }

                if (monthNumber == null) {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Invalid month name: $monthName"))
                    return@get
                }

                val transactions = db.getAllTransactions(email)
                val filteredTransactions = transactions.filter {
                    val transactionDate = LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))
                    transactionDate.month.value == monthNumber && transactionDate.year == year
                }
                call.respond(HttpStatusCode.OK, filteredTransactions)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "An error occurred"))
            }
        }

        delete<TransactionDeleteRoute> {
            val transactionId = try {
                call.request.queryParameters["id"]!!.toInt()
            } catch(e:Exception){
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "QueryParameter:id is not present"))
                return@delete
            }
            try {
                val email = call.principal<User>()!!.email
                db.deleteTransaction(transactionId, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "TX Deleted Succesfully"))
            }catch(e:Exception){
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem occured"))
            }
        }
        get<TransactionGetSumRoute> {
            try {
                val email = call.principal<User>()!!.email
                val transactions = db.getAllTransactions(email)
                val sum = transactions.sumOf{it.amount}
                call.respond(HttpStatusCode.OK,sum)
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Transaction>())
            }
        }
        get<TransactionGetCategoryRoute>{
            try {
                val category = call.request.queryParameters["category"]!!
                val email = call.principal<User>()!!.email
                val transactions = db.getAllTransactions(email)
                val sortedTransactions = transactions.filter { it.category == category }
                call.respond(HttpStatusCode.OK,sortedTransactions)
            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict, emptyList<Transaction>())
            }
        }
        get<TransactionGetCategorySumRoute>{
            try {
                val category = call.request.queryParameters["category"]!!
                val email = call.principal<User>()!!.email
                val transactions = db.getAllTransactions(email)
                val sortedTransactions = transactions.filter { it.category == category }
                val sum = sortedTransactions.sumOf{it.amount}
                call.respond(HttpStatusCode.OK,sum)
            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict, emptyList<Transaction>())
            }
        }
        get<TransactionGetTestRoute>{
            try {
                val response = ApiResponse(message ="JWT is valid", status=200)
                call.respond(HttpStatusCode.OK, response)
            } catch(e:Exception) {
                val response = ApiResponse(message ="JWT is invalid", status=200)
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}