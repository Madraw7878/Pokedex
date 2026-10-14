package com.example.pokedex.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    state: FavouritesUiState,
    onEvent: (FavouritesEvent) -> Unit,
    onItemClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favourites") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when (state) {
                FavouritesUiState.Empty -> Text("Nothing to see here yet")
                is FavouritesUiState.Success -> LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.id }) { p ->
                        Card(Modifier.fillMaxWidth().clickable { onItemClick(p.id) }) {
                            Row(
                                Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = p.imageUrl,
                                    contentDescription = p.name,
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        p.name.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("#${p.id}", style = MaterialTheme.typography.bodyMedium)
                                }
                                IconButton(onClick = { onEvent(FavouritesEvent.Remove(p.id)) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Remove from favs")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}