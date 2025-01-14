package com.example.pokeapi.ui.state

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.pokeapi.model.Question
import com.example.pokeapi.model.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

class UiFunctions(private val viewModel: PokeViewModel) {

    fun fetchQuestions(generationId: Int, questionCount: Int) {
        viewModel.viewModelScope.launch {
            viewModel.setPokeUiState(PokeUiState.Loading)
            try {
                // Llamada a la API para obtener los Pokémon por generación
                val generationResponse = RetrofitClient.apiService.getPokemonByGeneration(generationId)

                // Extraer los nombres de los Pokémon de la respuesta
                val pokemonNames = generationResponse.pokemon_species.map { it.name }

                // Generar preguntas basadas en los Pokémon obtenidos
                val questions = generateQuestions(pokemonNames, questionCount)
                if (questions.isNotEmpty()) {
                    viewModel.clearQuestions()
                    viewModel.addQuestions(questions)
                    viewModel.setTotalQuestions(questions.size)
                    viewModel.setPokeUiState(PokeUiState.Success(viewModel.questions))
                } else {
                    viewModel.setPokeUiState(PokeUiState.Error("No questions generated"))
                }
            } catch (e: Exception) {
                // Manejo de errores generales en la llamada a la API
                viewModel.setPokeUiState(PokeUiState.Error("Error fetching questions: ${e.message}"))
            }
        }
    }

    // Genera preguntas basadas en los Pokémon obtenidos
    private suspend fun generateQuestions(pokemonNames: List<String>, questionCount: Int): List<Question> {
        val questions = mutableListOf<Question>()

        // Seleccionamos los Pokémon al azar (limitado por la cantidad de preguntas)
        val selectedPokemon = pokemonNames.shuffled().take(questionCount)

        // Obtener los detalles de cada Pokémon de forma paralela
        val deferredQuestions = selectedPokemon.map { pokemonName ->
            viewModel.viewModelScope.async {
                try {
                    // Obtener los detalles del Pokémon por nombre
                    val pokemonDetails = RetrofitClient.apiService.getPokemonByName(pokemonName)

                    // Si obtenemos detalles del Pokémon correctamente
                    pokemonDetails?.let {
                        // Obtener el primer tipo del Pokémon
                        val type = it.types.first().type.name.capitalize()

                        // Opciones fijas para respuestas incorrectas (puedes hacerlas dinámicas si lo prefieres)
                        val options = listOf("Fire", "Water", "Grass", "Electric", "Normal")

                        // Filtramos las respuestas incorrectas para que no incluya el tipo correcto
                        val incorrectOptions = options.filter { it != type }

                        // Si no hay suficientes respuestas incorrectas, añadimos una opción por defecto
                        val finalIncorrectOptions = if (incorrectOptions.isNotEmpty()) {
                            incorrectOptions
                        } else {
                            listOf("Unknown") // Opción predeterminada si no hay respuestas incorrectas suficientes
                        }

                        // Crear la pregunta, ahora con el campo `type` incluido
                        return@async Question(
                            type = type, // Asignamos el tipo a la pregunta
                            question = "¿De qué tipo es el Pokémon $pokemonName?",
                            correct_answer = type,
                            incorrect_answers = finalIncorrectOptions
                        )
                    } ?: run {
                        // Si no obtenemos detalles, retornamos null
                        null
                    }
                } catch (e: Exception) {
                    // Si ocurre un error durante la llamada a la API
                    Log.e("UiFunctions", "Error fetching details for $pokemonName: ${e.message}")
                    null
                }
            }
        }

        // Esperamos que todos los detalles de los Pokémon sean procesados
        questions.addAll(deferredQuestions.awaitAll().filterNotNull())
        return questions
    }
}
