package com.example.pokemonexplorer.ui.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonexplorer.domain.model.Pokemon
import com.example.pokemonexplorer.domain.repository.PokemonRepository
import com.example.pokemonexplorer.ui.shared.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListState())
    val uiState = _uiState.asStateFlow()
    private var fullPokemonCache: List<Pokemon> = emptyList()
    private var currentPage = 0
    private val pageSize = 10

    val availableTypes = listOf(
        "Fire", "Water", "Grass", "Electric", "Dragon",
        "Psychic", "Ghost", "Dark", "Steel", "Fairy"
    )

    init {
        loadPokemonByType("Fire")
    }

    fun onEvent(event: PokemonListEvent) {
        when(event) {
            is PokemonListEvent.SelectType -> loadPokemonByType(event.type)
            is PokemonListEvent.LoadMore -> {
                currentPage++
                updateUiList()
            }
            is PokemonListEvent.Search -> {
                currentPage = 0
                _uiState.update { it.copy(searchQuery = event.query) }
                updateUiList()
            }
        }
    }

    private fun loadPokemonByType(type: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    selectedType = type,
                    error = null,
                    searchQuery = ""
                )
            }
            currentPage = 0

            when (val result = repository.getPokemonByType(type)) {
                is Resource.Success -> {
                    fullPokemonCache = result.data ?: emptyList()
                    updateUiList()
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message ?: "Unknown Error")
                    }
                }
                is Resource.Loading -> {
                    // Handled by isLoading flag above
                }
            }
        }
    }

    private fun updateUiList() {
        val query = _uiState.value.searchQuery

        val filteredList = if (query.isBlank()) {
            fullPokemonCache
        } else {
            fullPokemonCache.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        val totalItems = filteredList.size
        val maxItemIndex = (currentPage + 1) * pageSize
        val end = minOf(maxItemIndex, totalItems)

        if (filteredList.isEmpty()) {
            _uiState.update {
                it.copy(
                    pokemonList = emptyList(),
                    endReached = true,
                    isLoading = false
                )
            }
            return
        }

        val viewList = filteredList.subList(0, end)

        _uiState.update {
            it.copy(
                pokemonList = viewList,
                isLoading = false,
                endReached = end >= totalItems
            )
        }
    }
}