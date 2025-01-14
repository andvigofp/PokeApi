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
import com.example.pokeapi.ui.state.PokeUiState
import com.example.pokeapi.ui.state.PokeViewModel

@Composable
fun GameOverScreen(
    viewModel: PokeViewModel,
    generationId: Int,
    typeName: String,
    questionCount: Int,
    onHome: () -> Unit,
    onReplay: () -> Unit
) {
    // Calcula el porcentaje final de aciertos
    val finalScorePercentage = if (viewModel.totalQuestions > 0) {
        (viewModel.score * 100) / viewModel.totalQuestions
    } else {
        0
    }

    // Actualiza el récord si es necesario
    viewModel.checkAndUpdateRecord(finalScorePercentage)

    // Limitar el porcentaje entre 0 y 100
    val clampedScore = finalScorePercentage.coerceIn(0, 100)

    // Determinar el color de la puntuación final
    val scoreColor = when {
        clampedScore >= 80 -> MaterialTheme.colorScheme.primary // Alta puntuación: Verde
        clampedScore >= 50 -> MaterialTheme.colorScheme.secondary // Puntuación media
        else -> MaterialTheme.colorScheme.error // Baja puntuación: Rojo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Juego Terminado",
            style = MaterialTheme.typography.headlineMedium
        )

        // Color de fondo fijo para el porcentaje
        val darkVioletBackgroundColor = Color(0xFF8A2BE2) // Violeta oscuro
        val textColor = Color.White // Letras en blanco

        Text(
            text = "Puntuación final: $clampedScore%",
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier
                .background(darkVioletBackgroundColor)
                .padding(8.dp)
        )

        // Muestra el récord actualizado
        Text(
            text = "Récord: ${viewModel.record}%",
            style = MaterialTheme.typography.bodyLarge,
            color = scoreColor
        )

        // Botón para ir al inicio
        Button(
            onClick = {
                viewModel.checkAndUpdateRecord(clampedScore) // Asegura que el récord esté actualizado
                onHome()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Inicio")
        }

        // Botón para reiniciar el juego
        Button(
            onClick = {
                viewModel.startGame(generationId, typeName, questionCount) // Llamar a startGame con los parámetros correctos
                onReplay()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "Volver a jugar")
        }
    }
}
