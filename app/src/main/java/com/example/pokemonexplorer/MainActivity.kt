package com.example.pokemonexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.pokemonexplorer.ui.theme.PokemonExplorerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonExplorerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "pokemon_list") {

                    /*composable("pokemon_list") {
                        PokemonListScreen(
                            onNavigateToDetail = { name ->
                                navController.navigate("pokemon_detail/$name")
                            }
                        )
                    }

                    composable(
                        route = "pokemon_detail/{name}",
                        arguments = listOf(navArgument("name") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val name = backStackEntry.arguments?.getString("name") ?: return@composable
                        PokemonDetailScreen(
                            pokemonName = name,
                            onBack = { navController.popBackStack() }
                        )
                    }*/
                }
            }
        }
    }
}