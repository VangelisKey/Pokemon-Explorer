package com.example.pokemonexplorer.ui.feature.list

sealed class PokemonListEvent {
    data class SelectType(val type: String): PokemonListEvent()
    data class Search(val query: String): PokemonListEvent()
    object LoadMore: PokemonListEvent()
}