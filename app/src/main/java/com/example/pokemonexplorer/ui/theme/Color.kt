package com.example.pokemonexplorer.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

fun parseTypeToColor(type: String): Color {
    return when (type.lowercase()) {
        "fire" -> Color(0xFFFD4422)
        "water" -> Color(0xFF3398FD)
        "grass" -> Color(0xFF76CB55)
        "electric" -> Color(0xFFFDCB33)
        "psychic" -> Color(0xFFFD5598)
        "dark" -> Color(0xFF765544)
        "dragon" -> Color(0xFF7666ED)
        "fairy" -> Color(0xFFEC98ED)
        "ghost" -> Color(0xFF6666BA)
        "steel" -> Color(0xFFA9A9BA)
        "normal" -> Color(0xFFA9A9BA)
        "ice" -> Color(0xFF66CBFD)
        "fighting" -> Color(0xFFBA5544)
        "poison" -> Color(0xFFA95598)
        "ground" -> Color(0xFFDCBA55)
        "flying" -> Color(0xFF8798FD)
        "bug" -> Color(0xFFA9BA22)
        "rock" -> Color(0xFFBAA966)
        else -> Color(0xFFA9A998)
    }
}