package com.example.pokeapi.model

data class Question(
    val types: List<String>, // Ahora es una lista de tipos
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)
