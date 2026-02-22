package com.example.pokemonexplorer.ui.feature.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.pokemonexplorer.R
import com.example.pokemonexplorer.domain.model.Pokemon
import com.example.pokemonexplorer.ui.theme.parseTypeToColor

@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val themeColor by animateColorAsState(
        targetValue = if (state.selectedType == "All") Color(0xFF625b71) else parseTypeToColor(state.selectedType),
        animationSpec = tween(500), label = "themeColor"
    )

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(
            Brush.verticalGradient(
                colors = listOf(themeColor.copy(alpha = 0.15f), Color.White),
                startY = 0f,
                endY = 1000f
            )
        )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HomeHeader(
                searchQuery = state.searchQuery,
                onSearchChanged = { viewModel.onEvent(PokemonListEvent.Search(it)) }
            )

            TypeSelectorRow(
                types = viewModel.availableTypes,
                selectedType = state.selectedType,
                onTypeSelected = { viewModel.onEvent(PokemonListEvent.SelectType(it)) }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading && state.pokemonList.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = themeColor
                    )
                } else if (state.error != null) {
                    ErrorState(
                        error = state.error!!,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (state.pokemonList.isEmpty() && state.searchQuery.isNotEmpty()) {
                    EmptySearchState(
                        searchQuery = state.searchQuery,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    PokemonGrid(
                        pokemonList = state.pokemonList,
                        themeColor = themeColor,
                        isLoading = state.isLoading,
                        endReached = state.endReached || state.searchQuery.isNotBlank(),
                        onLoadMore = { viewModel.onEvent(PokemonListEvent.LoadMore) },
                        onPokemonClick = onNavigateToDetail
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(
    searchQuery: String,
    onSearchChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .statusBarsPadding()
    ) {
        Text(
            text = "Pokédex",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Search for Pokémon by name",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        TextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(100)),
            placeholder = { Text("What Pokémon are you looking for?") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Black.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.Black.copy(alpha = 0.05f)
            )
        )
    }
}

@Composable
fun TypeSelectorRow(
    types: List<String>,
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(types) { type ->
            val isSelected = type == selectedType
            val typeColor = if (type == "All") Color.DarkGray else parseTypeToColor(type)

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) typeColor else Color.Black.copy(alpha = 0.05f),
                label = "chipColor"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.Gray,
                label = "textColor"
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .clickable { onTypeSelected(type) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun PokemonGrid(
    pokemonList: List<Pokemon>,
    themeColor: Color,
    isLoading: Boolean,
    endReached: Boolean,
    onLoadMore: () -> Unit,
    onPokemonClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(pokemonList, key = { it.id }) { pokemon ->
            PokemonModernCard(
                pokemon = pokemon,
                cardColor = themeColor,
                onClick = { onPokemonClick(pokemon.name) }
            )
        }

        if (!endReached && pokemonList.isNotEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            color = themeColor
                        )
                    } else {
                        OutlinedButton(
                            onClick = onLoadMore,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = themeColor)
                        ) {
                            Text("Load More Pokémon")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonModernCard(
    pokemon: Pokemon,
    cardColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            cardColor.copy(alpha = 0.6f),
                            cardColor.copy(alpha = 0.9f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "#${pokemon.id.toString().padStart(3, '0')}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                )

                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    placeholder = painterResource(R.drawable.poke_ball_icon)
                )
            }
        }
    }
}

@Composable
fun ErrorState(error: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptySearchState(searchQuery: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No results",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We couldn't find any Pokémon matching \"$searchQuery\". Make sure the name is spelled correctly.",
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}