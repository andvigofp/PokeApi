package com.example.pokeapi.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.pokeapi.ui.state.PokeViewModel


@Composable
fun HomeScreen(
    viewModel: PokeViewModel,
    onStartGame: (Int, String, Int) -> Unit,
    onNavigateToGameOver: () -> Unit
) {
    var questionCount by remember { mutableStateOf(5) }  // Valor por defecto de 5 preguntas
    var generationId by remember { mutableStateOf(1) }   // Valor por defecto para la generación
    var typeName by remember { mutableStateOf("normal") }  // Valor por defecto para el tipo de Pokémon

    val gameOver = viewModel.gameOver
    val record = viewModel.record

    // Se ejecuta cuando el juego termina
    LaunchedEffect(gameOver) {
        if (gameOver) {
            onNavigateToGameOver() // Navegar a GameOverScreen
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround // Distribuye los elementos de forma uniforme
    ) {
        // Título en la parte superior de la pantalla
        Text(
            text = "PokeQuiz App",
            style = MaterialTheme.typography.titleLarge
        )

        // Espacio entre el título y el slider de generación
        Spacer(modifier = Modifier.height(16.dp))

        // Muestra el número de generación
        Text(text = "Generación: $generationId")

        // Slider para seleccionar la generación
        Slider(
            value = generationId.toFloat(),
            onValueChange = { generationId = it.toInt() },
            valueRange = 1f..8f,
            steps = 7,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Seleccionar generación" },
            enabled = !gameOver  // Deshabilitar el Slider cuando el juego haya terminado
        )

        // Espacio entre el slider de generación y el selector de tipo
        Spacer(modifier = Modifier.height(16.dp))

        // Muestra el tipo de Pokémon
        Text(text = "Tipo: $typeName")

        // Lista deslizante horizontal para la selección de tipo de Pokémon
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(
                listOf(
                    "normal", "fire", "water", "grass", "electric", "ice", "fighting", "poison",
                    "ground", "flying", "psychic", "bug", "rock", "ghost", "dark", "dragon",
                    "steel", "fairy"
                )
            ) { type ->
                Button(
                    onClick = { typeName = type },
                    modifier = Modifier
                        .padding(4.dp)
                        .height(50.dp)
                        .border(1.dp, Color.Black, shape = CircleShape) // Borde para los botones
                ) {
                    Text(
                        text = type.replaceFirstChar { it.uppercase() }, // Cambiar la primera letra a mayúscula
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Espacio entre el selector de tipo y el slider de número de preguntas
        Spacer(modifier = Modifier.height(16.dp))

        // Muestra el número de preguntas
        Text(text = "Preguntas: $questionCount")

        // Slider para seleccionar el número de preguntas
        Slider(
            value = questionCount.toFloat(),
            onValueChange = { questionCount = it.toInt() },
            valueRange = 5f..20f,
            steps = 15,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Seleccionar número de preguntas" },
            enabled = !gameOver  // Deshabilitar el Slider cuando el juego haya terminado
        )

        // Espacio entre el slider y el grupo de botones y récord
        Spacer(modifier = Modifier.height(16.dp))

        // Fila con el botón de jugar y el récord
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    viewModel.startGame(generationId, typeName, questionCount) // Inicia el juego con la generación, tipo y número de preguntas seleccionados
                    onStartGame(generationId, typeName, questionCount) // Pasa la información a la siguiente pantalla
                },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .semantics { contentDescription = "Iniciar juego" }
            ) {
                Text(text = "Jugar")
            }

            // Muestra el récord
            Text(
                text = "Récord: $record",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


