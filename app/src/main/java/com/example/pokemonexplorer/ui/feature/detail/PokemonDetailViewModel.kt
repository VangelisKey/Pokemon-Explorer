package com.example.pokemonexplorer.ui.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonexplorer.domain.model.PokemonDetail
import com.example.pokemonexplorer.domain.repository.PokemonRepository
import com.example.pokemonexplorer.ui.shared.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<PokemonDetail>>(Resource.Loading())
    val state = _state.asStateFlow()

    fun loadPokemonDetail(name: String) {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            _state.value = repository.getPokemonDetail(name)
        }
    }
}
