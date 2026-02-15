package com.example.pokemonexplorer.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String
)

data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val types: List<String>,
    val evolutions: List<Pokemon>
)