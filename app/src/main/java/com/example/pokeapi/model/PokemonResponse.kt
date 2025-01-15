package com.example.pokeapi.model


data class PokemonResponse(
    val name: String,
    val types: List<TypeInfo>
)

data class TypeInfo(
    val type: Type
)

data class Type(
    val name: String
)

data class GenerationResponse(
    val id: Int,
    val name: String,
    val pokemon_species: List<PokemonSpecies>
)

data class PokemonSpecies(
    val name: String
)

