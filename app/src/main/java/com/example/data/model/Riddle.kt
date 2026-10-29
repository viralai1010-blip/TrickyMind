package com.example.data.model

data class Riddle(
    val id: Int,
    val question: String,
    val options: List<String>,
    val answer: Int,
    val explanation: String
)
