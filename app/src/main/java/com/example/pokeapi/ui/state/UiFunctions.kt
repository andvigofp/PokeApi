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

    private suspend fun generateQuestions(
        pokemonNames: List<String>,
        questionCount: Int
    ): List<Question> {
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
                        // Obtener los tipos del Pokémon (pueden ser uno o dos)
                        val types = it.types.map { it.type.name.capitalize() }

                        // Lista de tipos posibles
                        val allTypes = listOf(
                            "Fire", "Water", "Grass", "Electric", "Normal", "Bug", "Ghost", "Fairy",
                            "Dragon", "Poison", "Fighting", "Ground", "Flying", "Psychic", "Rock",
                            "Steel", "Ice", "Dark"
                        )

                        // Filtramos las respuestas incorrectas para que no incluyan los tipos correctos
                        val incorrectOptions = allTypes.filter { it !in types }

                        // Si no hay suficientes respuestas incorrectas, añadimos una opción por defecto
                        val finalIncorrectOptions = if (incorrectOptions.size >= 3) {
                            incorrectOptions.shuffled()
                                .take(3) // Tomamos 3 opciones incorrectas al azar
                        } else {
                            allTypes.filter { it !in types } // Si no hay suficientes, usamos todas las demás opciones
                        }

                        // Crear la pregunta, ahora con el campo `types` incluido
                        return@async Question(
                            types = types, // Lista de tipos, que puede tener uno o dos tipos
                            question = "¿De qué tipo(s) es el Pokémon $pokemonName?",
                            correct_answer = types.joinToString(", "), // Los tipos correctos, separados por coma
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

        // Esperamos a que todas las preguntas se generen y las agregamos a la lista
        val generatedQuestions = deferredQuestions.awaitAll().filterNotNull()
        questions.addAll(generatedQuestions)

        return questions
    }
}
