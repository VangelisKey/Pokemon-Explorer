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

data class PokemonPaginatedResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NamedApiResourceDto>
)

data class PokemonBasicDto(
    val name: String,
    val url: String
)

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    @SerializedName("stats") val stats: List<PokemonStatDto>,
    @SerializedName("sprites") val sprites: PokemonSpritesDto,
    @SerializedName("types") val types: List<PokemonTypeSlotDto>,
    @SerializedName("species") val species: NamedApiResourceDto
)

data class PokemonTypeSlotDto(
    val slot: Int,
    val type: NamedApiResourceDto
)


data class NamedApiResourceDto(
    val name: String,
    val url: String
)

data class PokemonSpeciesDto(
    @SerializedName("evolution_chain") val evolutionChain: NamedApiResourceDto
)

data class EvolutionChainResponseDto(
    val chain: ChainLinkDto
)

data class ChainLinkDto(
    val species: NamedApiResourceDto,
    @SerializedName("evolves_to") val evolvesTo: List<ChainLinkDto>
)

data class PokemonStatDto(
    @SerializedName("base_stat") val value: Int,
    @SerializedName("stat") val statInfo: PokemonStatInfo
)
data class PokemonStatInfo(val name: String)
data class PokemonSpritesDto(@SerializedName("front_default") val frontDefault: String?)