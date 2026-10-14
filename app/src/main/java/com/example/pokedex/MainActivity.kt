package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokedex.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val nav = rememberNavController()
                NavHost(navController = nav, startDestination = "list") {
                    composable("list") {
                        val vm: ListViewModel = viewModel()
                        val state by vm.state.collectAsState()
                        ListScreen(
                            state = state,
                            onEvent = vm::onEvent,
                            onItemClick = { id -> nav.navigate("detail/$id") },
                            onFavouritesClick = { nav.navigate("favourites") }
                        )
                    }
                    composable(
                        route = "detail/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) {
                        val vm: DetailViewModel = viewModel()
                        val state by vm.state.collectAsState()
                        DetailScreen(
                            state = state,
                            onEvent = vm::onEvent,
                            onBack = { nav.popBackStack() }
                        )
                    }
                    composable("favourites") {
                        val vm: FavouritesViewModel = viewModel()
                        val state by vm.state.collectAsState()
                        FavouritesScreen(
                            state = state,
                            onEvent = vm::onEvent,
                            onItemClick = { id -> nav.navigate("detail/$id") },
                            onBack = { nav.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}