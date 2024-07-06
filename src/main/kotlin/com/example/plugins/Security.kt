package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.authentication.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

//fun Application.configureSecurity() {
//    data class MySession(val count: Int = 0)
//    install(Sessions) {
//        cookie<MySession>("MY_SESSION") {
//            cookie.extensions["SameSite"] = "lax"
//        }
//    }
//    val jwtService = JwtService()
//    install(Authentication) {
//        jwt("jwt") {
//            verifier(jwtService.varifier) // сомнительно но окэй
//            realm = "Ktor Ledger"
//            validate {
//                val payload = it.payload
//                val email = payload.getClaim("email").asString()
//                val user = db.findUserByEmail(email)
//            }
//        }
//    }
//
//}
