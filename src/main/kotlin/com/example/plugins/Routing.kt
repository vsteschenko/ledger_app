package com.example.plugins

import com.example.authentication.JwtService
import com.example.authentication.hash
import com.example.data.model.User
import com.example.repository.Repo
import com.example.routes.TransactionRoutes
import com.example.routes.UserRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureRouting() {
    val db = Repo()
    val jwtService = JwtService()
    val hashFunction = { s:String -> hash(s) }

    data class MySession(val count: Int = 0)
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.varifier) // сомнительно но окэй
            realm = "Ktor Ledger"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user
            }
        }
    }

    routing {

        UserRoutes(db,jwtService,hashFunction)
        TransactionRoutes(db,hashFunction)

        get("/") {
            call.respondText("Hello World!")
        }

        get("/note/{id}") {
            val id = call.parameters["id"]
            call.respond("$id")
        }

        get("/token") {
            val email = call.request.queryParameters["email"]!!
            val password = call.request.queryParameters["password"]!!
            val username = call.request.queryParameters["username"]!!

            val user = User(email, hashFunction(password), username)
            call.respond(jwtService.generateToken(user))
        }

        get("/note") {
            val id = call.request.queryParameters["id"]
            call.respond("$id")
        }

        route("/notes") {

            route("/create") {

                post{
                    val body = call.receive<String>()
                    call.respond(body)
                }
            }

            delete{
                val body = call.receive<String>()
                call.respond(body)
            }
        }
    }
}
