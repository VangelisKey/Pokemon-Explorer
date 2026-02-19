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

    // Cache for loaded items (acts as the full list for Types, or accumulated pages for "All")
    private var fullPokemonCache: List<Pokemon> = emptyList()

    private var currentPage = 0 // Used for manual type paging
    private var currentOffset = 0 // Used for real API paging
    private val pageSize = 10

    val availableTypes = listOf(
        "All", // Added "All" category
        "Fire", "Water", "Grass", "Electric", "Dragon",
        "Psychic", "Ghost", "Dark", "Steel", "Fairy"
    )

    init {
        loadPokemonByType("All")
    }

    fun onEvent(event: PokemonListEvent) {
        when(event) {
            is PokemonListEvent.SelectType -> {
                _uiState.update { it.copy(searchQuery = "") }
                loadPokemonByType(event.type)
            }
            is PokemonListEvent.LoadMore -> {
                if (_uiState.value.selectedType == "All") {
                    currentOffset += pageSize
                    loadAllPokemon(currentOffset)
                } else {
                    currentPage++
                    updateUiList()
                }
            }
            is PokemonListEvent.Search -> {
                if (_uiState.value.selectedType != "All") {
                    currentPage = 0
                }
                _uiState.update { it.copy(searchQuery = event.query) }
                updateUiList()
            }
        }
    }

    private fun loadPokemonByType(type: String) {
        _uiState.update {
            it.copy(isLoading = true, selectedType = type, error = null, pokemonList = emptyList())
        }

        currentPage = 0
        currentOffset = 0
        fullPokemonCache = emptyList()

        if (type == "All") {
            loadAllPokemon(currentOffset)
        } else {
            viewModelScope.launch {
                when (val result = repository.getPokemonByType(type)) {
                    is Resource.Success -> {
                        fullPokemonCache = result.data ?: emptyList()
                        updateUiList()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun loadAllPokemon(offset: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getAllPokemon(offset, pageSize)) {
                is Resource.Success -> {
                    val newItems = result.data ?: emptyList()
                    val isEndReached = newItems.size < pageSize

                    fullPokemonCache = fullPokemonCache + newItems

                    _uiState.update { it.copy(endReached = isEndReached) }
                    updateUiList()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun updateUiList() {
        val query = _uiState.value.searchQuery
        val isAllType = _uiState.value.selectedType == "All"

        // Filter based on search query
        val filteredList = if (query.isBlank()) {
            fullPokemonCache
        } else {
            fullPokemonCache.filter { it.name.contains(query, ignoreCase = true) }
        }

        if (isAllType) {
            _uiState.update {
                it.copy(
                    pokemonList = filteredList,
                    isLoading = false
                )
            }
        } else {
            val totalItems = filteredList.size
            val maxItemIndex = (currentPage + 1) * pageSize
            val end = minOf(maxItemIndex, totalItems)

            if (filteredList.isEmpty()) {
                _uiState.update { it.copy(pokemonList = emptyList(), endReached = true, isLoading = false) }
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
}