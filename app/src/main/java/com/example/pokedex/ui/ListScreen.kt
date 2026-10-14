package com.example.pokedex.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokedex.data.PokemonItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    state: ListUiState,
    onEvent: (ListEvent) -> Unit,
    onItemClick: (Int) -> Unit,
    onFavouritesClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokedex") },
                actions = {
                    IconButton(onClick = onFavouritesClick) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Favourites")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { onEvent(ListEvent.QueryChanged(it)) },
                label = { Text("Search by name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when (val c = state.content) {
                    ListContent.Loading -> CircularProgressIndicator()
                    is ListContent.Error -> ErrorView(c.message) { onEvent(ListEvent.Retry) }
                    ListContent.Empty -> Text("Nothing is found")
                    is ListContent.Success -> PokemonList(c.items, onItemClick)
                }
            }
        }
    }
}

@Composable
private fun PokemonList(items: List<PokemonItem>, onItemClick: (Int) -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { p ->
            Card(Modifier.fillMaxWidth().clickable { onItemClick(p.id) }) {
                Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = p.imageUrl,
                        contentDescription = p.name,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            p.name.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("#${p.id}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
        Text("Error: $message")
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}