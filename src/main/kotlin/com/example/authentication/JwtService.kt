package com.example.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.User
import java.util.Date

class JwtService {
    private val issuer = "KotlinServer"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    private val expirationTime = 60 * 60 * 1000

    val varifier:JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: User):String {
        val expirationDate = Date(System.currentTimeMillis() + expirationTime)
        return JWT.create()
            .withSubject("KtorAuthentication")
            .withIssuer(issuer)
            .withClaim("email", user.email)
            .withExpiresAt(expirationDate)
            .sign(algorithm)
    }
}