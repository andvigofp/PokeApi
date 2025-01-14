package com.example.pokeapi.data

import com.example.pokeapi.model.Type

data class PokemonDetails(
    val id: Int, val name: String,
    val types: List<PokemonTypeSlot>
)

data class PokemonTypeSlot(
    val slot: Int,
    val type: Type
)
