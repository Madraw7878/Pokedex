package com.example.pokedex.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object FavouritesRep {
    private val _items = MutableStateFlow<List<PokemonItem>>(emptyList())
    val items: StateFlow<List<PokemonItem>> = _items

    fun toggle(item: PokemonItem) {
        _items.update { list ->
            if (list.any { it.id == item.id }) list.filterNot { it.id == item.id }
            else list + item
        }
    }

    fun remove(id: Int) {
        _items.update { list -> list.filterNot { it.id == id } }
    }
}