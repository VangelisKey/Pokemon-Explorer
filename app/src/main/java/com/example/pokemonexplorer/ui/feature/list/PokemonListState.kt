package com.example.pokemonexplorer.ui.feature.list

import com.example.pokemonexplorer.domain.model.Pokemon

data class PokemonListState(
    val selectedType: String = "All",
    val pokemonList: List<Pokemon> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false
)