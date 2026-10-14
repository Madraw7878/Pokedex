package com.example.pokedex.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.FavouritesRep
import com.example.pokedex.data.PokemonItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface FavouritesUiState {
    data object Empty : FavouritesUiState
    data class Success(val items: List<PokemonItem>) : FavouritesUiState
}

sealed interface FavouritesEvent {
    data class Remove(val id: Int) : FavouritesEvent
}

class FavouritesViewModel : ViewModel() {

    val state: StateFlow<FavouritesUiState> = FavouritesRep.items
        .map { toState(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            toState(FavouritesRep.items.value)
        )

    fun onEvent(event: FavouritesEvent) {
        when (event) {
            is FavouritesEvent.Remove -> FavouritesRep.remove(event.id)
        }
    }

    private fun toState(list: List<PokemonItem>): FavouritesUiState =
        if (list.isEmpty()) FavouritesUiState.Empty else FavouritesUiState.Success(list)
}