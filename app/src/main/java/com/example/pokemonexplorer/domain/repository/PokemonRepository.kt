package com.example.pokemonexplorer.domain.repository

import com.example.pokemonexplorer.domain.model.Pokemon
import com.example.pokemonexplorer.domain.model.PokemonDetail
import com.example.pokemonexplorer.ui.shared.Resource

interface PokemonRepository {
    suspend fun getPokemonByType(type: String): Resource<List<Pokemon>>
    suspend fun getPokemonDetail(name: String): Resource<PokemonDetail>
}