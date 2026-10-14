package com.example.pokedex.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonItem
import com.example.pokedex.data.PokemonRep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ListContent {
    data object Loading : ListContent
    data class Error(val message: String) : ListContent
    data object Empty : ListContent
    data class Success(val items: List<PokemonItem>) : ListContent
}

data class ListUiState(
    val query: String = "",
    val content: ListContent = ListContent.Loading
)

sealed interface ListEvent {
    data class QueryChanged(val query: String) : ListEvent
    data object Retry : ListEvent
}

class ListViewModel(
    private val repo: PokemonRep = PokemonRep()
) : ViewModel() {

    private val _state = MutableStateFlow(ListUiState())
    val state: StateFlow<ListUiState> = _state

    //у PokeAPI нету серверного поиска, загрузила список один раз и локально отфильтровала
    private var allItems: List<PokemonItem> = emptyList()

    init { load() }

    fun onEvent(event: ListEvent) {
        when (event) {
            is ListEvent.QueryChanged -> {
                _state.update { it.copy(query = event.query) }
                applyFilter()
            }
            ListEvent.Retry -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(content = ListContent.Loading) }
            runCatching { repo.getAll() }.fold(
                onSuccess = { allItems = it; applyFilter() },
                onFailure = { e ->
                    _state.update { it.copy(content = ListContent.Error(e.message ?: "Network error")) }
                }
            )
        }
    }

    private fun applyFilter() {
        val q = _state.value.query.trim()
        val filtered = if (q.isEmpty()) allItems
        else allItems.filter { it.name.contains(q, ignoreCase = true) }
        _state.update {
            it.copy(content = if (filtered.isEmpty()) ListContent.Empty else ListContent.Success(filtered))
        }
    }
}
