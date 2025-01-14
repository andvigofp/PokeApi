package com.example.pokeapi.ui.state

import com.example.pokeapi.model.Question

sealed class PokeUiState {
    object Loading : PokeUiState()
    object Playing : PokeUiState() // El estado cuando se está jugando
    object GameOver : PokeUiState() // El estado cuando el juego ha terminado
    data class Success(val questions: List<Question>) : PokeUiState()
    data class Error(val message: String) : PokeUiState() // Puedes incluir mensajes de error
}