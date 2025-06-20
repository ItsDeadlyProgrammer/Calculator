package com.example.calculator.models

data class Calculation(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)