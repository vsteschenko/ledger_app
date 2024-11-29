package com.example.data.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Transaction(
    val id: Int,
    val amount: BigDecimal,
    val category: String,
    val location: String,
    val comment: String,
    val userEmail: String,
    val date: LocalDateTime,
)