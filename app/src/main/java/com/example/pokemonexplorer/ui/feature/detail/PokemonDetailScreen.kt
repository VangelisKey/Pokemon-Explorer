package com.example.pokemonexplorer.ui.feature.detail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.pokemonexplorer.domain.model.Pokemon
import com.example.pokemonexplorer.domain.model.PokemonDetail
import com.example.pokemonexplorer.ui.shared.Resource
import com.example.pokemonexplorer.ui.theme.parseTypeToColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonName: String,
    onBack: () -> Unit,
    onPokemonClick: (String) -> Unit,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(pokemonName) {
        viewModel.loadPokemonDetail(pokemonName)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (state is Resource.Success) Color.White else Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val result = state) {
                is Resource.Loading -> CircularProgressIndicator()
                is Resource.Error -> Text("Error: ${result.message}", color = Color.Red)
                is Resource.Success -> {
                    val detail = result.data!!
                    val primaryColor = parseTypeToColor(detail.types.firstOrNull() ?: "normal")

                    DetailContent(
                        detail = detail,
                        primaryColor = primaryColor,
                        onPokemonClick = onPokemonClick,
                        contentPadding = padding
                    )
                }
            }
        }
    }
}

@Composable
fun DetailContent(
    detail: PokemonDetail,
    primaryColor: Color,
    onPokemonClick: (String) -> Unit,
    contentPadding: PaddingValues
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(primaryColor, Color.White),
                    startY = 0f,
                    endY = 1500f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
            
            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = detail.imageUrl,
                contentDescription = detail.name,
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = detail.name,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(modifier = Modifier.padding(top = 16.dp)) {
                detail.types.forEach { type ->
                    TypeChip(type)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Base Stats",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    StatBarAnimated("HP", detail.hp, primaryColor)
                    StatBarAnimated("ATK", detail.attack, Color(0xFFFF5252))
                    StatBarAnimated("DEF", detail.defense, Color(0xFF448AFF))

                    Spacer(modifier = Modifier.height(32.dp))

                    if (detail.evolutions.isNotEmpty()) {
                        Text(
                            text = "Evolution Chain",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(detail.evolutions) { evolution ->
                                EvolutionItem(
                                    pokemon = evolution,
                                    isCurrent = evolution.name.equals(detail.name, ignoreCase = true),
                                    onClick = { onPokemonClick(evolution.name) }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding()))
        }
    }
}

@Composable
fun TypeChip(type: String) {
    Surface(
        color = parseTypeToColor(type),
        shape = RoundedCornerShape(50),
        shadowElevation = 4.dp
    ) {
        Text(
            text = type.uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun StatBarAnimated(name: String, value: Int, color: Color) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(value) {
        progress.animateTo(
            targetValue = value / 200f, // Stat scaling (max 200)
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.width(50.dp)
        )
        Text(
            text = "$value",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.width(40.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.value)
                    .clip(RoundedCornerShape(5.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun EvolutionItem(
    pokemon: Pokemon,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(
                    if (isCurrent) Color.Black.copy(alpha = 0.05f) else Color.Transparent
                )
                .padding(8.dp)
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = pokemon.name,
            fontSize = 12.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (isCurrent) Color.Black else Color.Gray,
            maxLines = 1
        )
    }
}
