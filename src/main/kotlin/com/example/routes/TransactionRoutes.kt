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
import kotlinx.coroutines.channels.BroadcastChannel
import java.time.LocalDateTime

const val TXS = "$API_VERSION/txs"
const val CREATE_TXS = "$TXS/create"
const val UPDATE_TXS = "$TXS/update"
const val DELETE_TXS = "$TXS/delete"

@Location(CREATE_TXS)
class TransactionCreateRoute

@Location(UPDATE_TXS)
class TransactionUpdateRoute

@Location(DELETE_TXS)
class TransactionDeleteRoute

@Location(TXS)
class TransactionGetRoute

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
                val currentTime = LocalDateTime.now().toString() // FIX DATE AND TIME
                db.addTransaction(transaction, email, currentTime)
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
    }
}