package com.example.pokemonexplorer.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface PokeApi {
    @GET("type/{name}")
    suspend fun getPokemonByType(@Path("name") type: String): TypeResponseDto

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(@Path("name") name: String): PokemonDetailDto

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): PokemonSpeciesDto

    @GET
    suspend fun getEvolutionChain(@Url url: String): EvolutionChainResponseDto
}