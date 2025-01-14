package com.example.pokeapi.model


data class PokemonResponse(
    val name: String,
    val types: List<Type>
)

data class Type(
    val type: TypeName
)

data class TypeName(
    val name: String
)


