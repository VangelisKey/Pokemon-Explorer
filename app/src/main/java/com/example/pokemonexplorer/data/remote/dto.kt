package com.example.pokemonexplorer.data.remote

import com.google.gson.annotations.SerializedName

data class TypeResponseDto(
    @SerializedName("pokemon")
    val pokemon: List<PokemonWrapperDto>
)

data class PokemonWrapperDto(
    @SerializedName("pokemon")
    val data: PokemonBasicDto
)

data class PokemonBasicDto(
    val name: String,
    val url: String
)

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    @SerializedName("stats") val stats: List<PokemonStatDto>,
    @SerializedName("sprites") val sprites: PokemonSpritesDto
)

data class PokemonStatDto(
    @SerializedName("base_stat") val value: Int,
    @SerializedName("stat") val statInfo: PokemonStatInfo
)

data class PokemonStatInfo(
    val name: String
)

data class PokemonSpritesDto(
    @SerializedName("front_default") val frontDefault: String?
)