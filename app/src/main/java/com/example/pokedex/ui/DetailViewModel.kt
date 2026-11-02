package com.example.pokedex.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.FavouritesRep
import com.example.pokedex.data.PokemonDetailDto
import com.example.pokedex.data.PokemonItem
import com.example.pokedex.data.PokemonRep
import com.example.pokedex.data.imageUrlFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Error(val message: String) : DetailUiState
    data class Success(val item: PokemonDetailDto, val isFavourite: Boolean) : DetailUiState
}

sealed interface DetailEvent {
    data object Retry : DetailEvent
    data object ToggleFavourite : DetailEvent
}

class DetailViewModel @JvmOverloads constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: PokemonRep = PokemonRep()
) : ViewModel() {

    private val id: Int = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    private var loadJob: Job? = null
    val state: StateFlow<DetailUiState> = _state

    init {
        load()
        // следим за избранным, чтобы сердечко обновлялось
        viewModelScope.launch {
            FavouritesRep.items.collect { list ->
                val fav = list.any { it.id == id }
                _state.update { s ->
                    if (s is DetailUiState.Success) s.copy(isFavourite = fav) else s
                }
            }
        }
    }

    fun onEvent(event: DetailEvent) {
        when (event) {
            DetailEvent.Retry -> load()
            DetailEvent.ToggleFavourite -> {
                val s = _state.value
                if (s is DetailUiState.Success) {
                    FavouritesRep.toggle(
                        PokemonItem(s.item.id, s.item.name, imageUrlFor(s.item.id))
                    )
                }
            }
        }
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = DetailUiState.Loading
            try {
                val item = repo.getById(id)
                val fav = FavouritesRep.items.value.any { it.id == id }
                _state.value = DetailUiState.Success(item, fav)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = DetailUiState.Error(e.message ?: "Ошибка сети")
            }
        }
    }
}
