package com.example.pokeapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokeapi.ui.navigation.PokeApiApp
import com.example.pokeapi.ui.state.PokeViewModel
import com.example.pokeapi.ui.theme.PokeApiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeApiTheme {
                val viewModel: PokeViewModel = viewModel()
                PokeApiApp(viewModel)

                }
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokeApiTheme {
        PokeApiApp()
    }
}