package com.example.pokemonexplorer.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {
    @GET("type/{name}")
    suspend fun getPokemonByType(@Path("name") type: String): TypeResponseDto

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(@Path("name") name: String): PokemonDetailDto
}