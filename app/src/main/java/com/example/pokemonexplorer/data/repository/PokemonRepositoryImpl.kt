package com.example.pokemonexplorer.data.repository

import com.example.pokemonexplorer.data.remote.ChainLinkDto
import com.example.pokemonexplorer.data.remote.PokeApi
import com.example.pokemonexplorer.domain.model.Pokemon
import com.example.pokemonexplorer.domain.model.PokemonDetail
import com.example.pokemonexplorer.domain.repository.PokemonRepository
import com.example.pokemonexplorer.ui.shared.Resource
import jakarta.inject.Inject
import retrofit2.HttpException
import java.io.IOException

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi
) : PokemonRepository {

    override suspend fun getAllPokemon(offset: Int, limit: Int): Resource<List<Pokemon>> {
        return try {
            val response = api.getAllPokemon(offset, limit)

            val pokemonList = response.results.map { resource ->
                val url = resource.url
                val id = if (url.endsWith("/")) {
                    url.dropLast(1).takeLastWhile { it.isDigit() }.toInt()
                } else {
                    url.takeLastWhile { it.isDigit() }.toInt()
                }

                val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

                Pokemon(
                    id = id,
                    name = resource.name.replaceFirstChar { it.uppercase() },
                    imageUrl = imageUrl
                )
            }

            Resource.Success(pokemonList)
        } catch (e: HttpException) {
            Resource.Error("Oops! Something went wrong on our end. Please try again later.")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach the server. Check your internet connection.")
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown Error"}")
        }
    }

    override suspend fun getPokemonByType(type: String): Resource<List<Pokemon>> {
        return try {
            val response = api.getPokemonByType(type.lowercase())

            val pokemonList = response.pokemon.map { wrapper ->
                val name = wrapper.data.name
                val url = wrapper.data.url
                val id = if (url.endsWith("/")) {
                    url.dropLast(1).takeLastWhile { it.isDigit() }
                } else {
                    url.takeLastWhile { it.isDigit() }
                }.toInt()

                val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

                Pokemon(
                    id = id,
                    name = name.replaceFirstChar { it.uppercase() },
                    imageUrl = imageUrl
                )
            }

            Resource.Success(pokemonList)

        } catch (e: HttpException) {
            Resource.Error("Oops! Something went wrong on our end. Please try again later.")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach the server. Check your internet connection.")
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown Error"}")
        }
    }

    override suspend fun getPokemonDetail(name: String): Resource<PokemonDetail> {
        return try {
            val dto = api.getPokemonDetail(name.lowercase())

            val types = dto.types.map { it.type.name }
            val hp = dto.stats.find { it.statInfo.name == "hp" }?.value ?: 0
            val attack = dto.stats.find { it.statInfo.name == "attack" }?.value ?: 0
            val defense = dto.stats.find { it.statInfo.name == "defense" }?.value ?: 0

            val imageUrl = dto.sprites.frontDefault
                ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${dto.id}.png"

            val evolutions = try {
                val speciesDto = api.getPokemonSpecies(dto.species.name)
                val chainDto = api.getEvolutionChain(speciesDto.evolutionChain.url)
                parseEvolutionChain(chainDto.chain)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }

            val detail = PokemonDetail(
                id = dto.id,
                name = dto.name.replaceFirstChar { it.uppercase() },
                imageUrl = imageUrl,
                hp = hp,
                attack = attack,
                defense = defense,
                types = types,
                evolutions = evolutions
            )

            Resource.Success(detail)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                Resource.Error("We couldn't find any details for '$name'.")
            } else {
                Resource.Error("Oops! Something went wrong on our end. Please try again later.")
            }
        } catch (e: IOException) {
            Resource.Error("Couldn't reach the server. Check your internet connection.")
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown Error"}")
        }
    }

    private fun parseEvolutionChain(link: ChainLinkDto): List<Pokemon> {
        val currentList = mutableListOf<Pokemon>()
        val id = link.species.url.dropLast(1).takeLastWhile { it.isDigit() }.toInt()

        currentList.add(
            Pokemon(
                id = id,
                name = link.species.name.replaceFirstChar { it.uppercase() },
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
            )
        )

        link.evolvesTo.forEach { childLink ->
            currentList.addAll(parseEvolutionChain(childLink))
        }

        return currentList
    }
}
