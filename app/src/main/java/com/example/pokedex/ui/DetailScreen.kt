package com.example.pokedex.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokedex.data.imageUrlFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailUiState,
    onEvent: (DetailEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Info") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state is DetailUiState.Success) {
                        IconButton(onClick = { onEvent(DetailEvent.ToggleFavourite) }) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "Favourites",
                                tint = if (state.isFavourite) Color.Red else Color.Gray
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when (state) {
                DetailUiState.Loading -> CircularProgressIndicator()
                is DetailUiState.Error ->
                    ErrorView(state.message) { onEvent(DetailEvent.Retry) }
                is DetailUiState.Success -> {
                    val p = state.item
                    Column(
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = imageUrlFor(p.id),
                            contentDescription = p.name,
                            modifier = Modifier.size(240.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${p.name.replaceFirstChar { it.uppercase() }} #${p.id}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Types: ${p.types.joinToString { it.type.name }}")
                        Text("Height: ${p.height / 10.0} m")
                        Text("Weight: ${p.weight / 10.0} kg")
                        Text("Abilities: ${p.abilities.joinToString { it.ability.name }}")
                        Spacer(Modifier.height(12.dp))
                        Text("Stats", style = MaterialTheme.typography.titleMedium)
                        p.stats.forEach { s ->
                            Text("${s.stat.name}: ${s.baseStat}")
                        }
                    }
                }
            }
        }
    }
}