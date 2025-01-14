package com.example.pokeapi.model

data class Question(
    val type: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)