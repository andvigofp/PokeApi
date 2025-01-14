package com.example.pokeapi.network



import com.example.pokeapi.model.GenerationResponse
import com.example.pokeapi.model.PokemonResponse

import retrofit2.http.GET
import retrofit2.http.Path



interface PokeApiService {

    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): PokemonResponse

    @GET("generation/{id}")
    suspend fun getPokemonByGeneration(@Path("id") id: Int): GenerationResponse
}






