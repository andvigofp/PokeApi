package com.example.pokeapi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pokeapi.ui.navigation.PokeApiSreen
import com.example.pokeapi.ui.state.PokeUiState
import com.example.pokeapi.ui.state.PokeViewModel


@Composable
fun GameScreen(
    viewModel: PokeViewModel,
    generationId: Int,
    typeName: String,
    questionCount: Int,
    onGameOver: () -> Unit,
    navController: NavController
) {
    // Inicializar el juego si las preguntas están vacías
    LaunchedEffect(questionCount) {
        if (viewModel.questions.isEmpty()) {
            viewModel.startGame(generationId, typeName, questionCount)
        }
    }

    // Obtenemos el estado de la UI
    val pokeUiState = viewModel.pokeUiState.value

    when (pokeUiState) {
        // Estado de carga
        is PokeUiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(text = "Cargando preguntas...", style = MaterialTheme.typography.bodyLarge)
            }
        }
        // Estado de error
        is PokeUiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error al cargar las preguntas. Por favor, intenta nuevamente.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }
        }
        // Estado de éxito, cuando las preguntas han cargado
        is PokeUiState.Success -> {
            val questions = viewModel.questions
            val currentQuestionIndex = viewModel.currentQuestionIndex
            val question = questions.getOrNull(currentQuestionIndex)

            // Si no hay una pregunta válida, mostrar mensaje de error
            if (question == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error al cargar la pregunta. Por favor, reinicia el juego.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red
                    )
                }
                return
            }

            // Estados locales para la respuesta seleccionada y si se muestra el resultado
            var selectedAnswerIndex by remember { mutableStateOf(-1) }
            var showResult by remember { mutableStateOf(false) }

            // Estado para manejar la transición a GameOver
            var gameOverNavigated by remember { mutableStateOf(false) }

            // Layout principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                // Mostrar el número de la pregunta
                Text(text = "Pregunta ${currentQuestionIndex + 1}/$questionCount")

                // Mostrar la pregunta
                Text(text = question.question, style = MaterialTheme.typography.bodyLarge)

                // Barajar las opciones de respuesta
                val shuffledOptions = remember(question) { (question.incorrect_answers + question.correct_answer).shuffled() }

                // Mostrar las opciones de respuesta
                shuffledOptions.forEachIndexed { index, option ->

                    val backgroundColor = when {
                        !showResult -> MaterialTheme.colorScheme.surface
                        index == shuffledOptions.indexOf(question.correct_answer) -> Color.Green
                        index == selectedAnswerIndex -> Color.Red
                        else -> Color.Gray
                    }

                    val borderColor = when {
                        !showResult -> MaterialTheme.colorScheme.surface
                        index == shuffledOptions.indexOf(question.correct_answer) -> Color.Green
                        index == selectedAnswerIndex -> Color.Red
                        else -> Color.Black
                    }

                    val textColor = when {
                        index == selectedAnswerIndex && index != shuffledOptions.indexOf(question.correct_answer) -> Color.White
                        else -> Color.Black
                    }

                    // Botón para cada opción
                    Button(
                        onClick = {
                            if (!showResult) {
                                selectedAnswerIndex = index
                                showResult = true
                                viewModel.submitAnswer(index, shuffledOptions)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(backgroundColor, RoundedCornerShape(8.dp))
                            .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
                        enabled = !showResult
                    ) {
                        Text(text = option, color = textColor)
                    }
                }

                // Mostrar el resultado después de seleccionar una respuesta
                if (showResult) {
                    Text(
                        text = if (selectedAnswerIndex == shuffledOptions.indexOf(question.correct_answer))
                            "¡Correcto!"
                        else
                            "Incorrecto. La respuesta correcta es: ${question.correct_answer}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Botón para pasar a la siguiente pregunta o terminar el juego
                    Button(
                        onClick = {
                            if (currentQuestionIndex + 1 >= questionCount) {
                                // Actualiza el récord cuando el juego termina
                                viewModel.updateRecordAndNavigate(navController)

                                // Solo navegar si no se ha navegado previamente
                                if (!gameOverNavigated) {
                                    gameOverNavigated = true
                                    // Navegar inmediatamente a la pantalla GameOver
                                    navController.navigate("gameOver") {
                                        // Usar popUpTo para evitar que se acumulen pantallas
                                        popUpTo("gameScreen") { inclusive = true }
                                    }
                                    // Llamar a onGameOver() para finalizar el juego
                                    onGameOver()
                                }
                            } else {
                                viewModel.moveToNextQuestion(navController)
                                selectedAnswerIndex = -1
                                showResult = false
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp),
                        enabled = showResult
                    ) {
                        Text(text = "Siguiente")
                    }
                }

                // Mostrar puntuación actual
                Text(text = "Puntuación: ${viewModel.score}/$questionCount")
            }
        }
        else -> {
            // Estado desconocido
            Text(text = "Estado desconocido", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
