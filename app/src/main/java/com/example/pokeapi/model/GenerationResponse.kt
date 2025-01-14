package com.example.pokeapi.model

data class GenerationResponse(
    val pokemon_species: List<PokemonSpecies>  // Aquí están los Pokémon de la generación
)

data class PokemonSpecies(
    val name: String,
    val url: String
)
