package com.example.pokemonexplorer.di

import com.example.pokemonexplorer.data.remote.PokeApi
import com.example.pokemonexplorer.data.repository.PokemonRepositoryImpl
import com.example.pokemonexplorer.domain.repository.PokemonRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePokeApi(): PokeApi {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApi::class.java)
    }
    
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryModule {

        @Binds
        @Singleton
        abstract fun bindPokemonRepository(
            pokemonRepositoryImpl: PokemonRepositoryImpl
        ): PokemonRepository
    }
}
