package com.bragonya.daggerdemo.ui.detail

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bragonya.daggerdemo.model.*
import com.bragonya.daggerdemo.repositories.PokeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PokemonDetailViewModel @ViewModelInject constructor(private val pokeRepository: PokeRepository): ViewModel() {

    val pokemon = MutableLiveData<PokemonDetailInformation>()

    fun getPokemon(id: Int) = viewModelScope.launch {
        val pokemonDetail = async { pokeRepository.getPokemonDetailsFromNetwork(id) }
        val pokemonSpecie = pokeRepository.getPokemonSpecie(id)
        val number = pokemonSpecie.evolutionChanURL.url
            .split("/")
            .last { it.isNotEmpty() }
            .toInt()
        val pokemonEvolutionChain = async { pokeRepository.getPokemonEvolutionChain(number) }
        pokemon.postValue(PokemonDetailInformation(
            pokemonDetail.await(),
            pokemonSpecie,
            processEvolutionChain(pokemonEvolutionChain.await().chain)
        ))

    }

    inner class PokemonDetailInformation(
        val pokemonDetail:PokemonDetail,
        val pokemonSpecie: PokemonSpecie,
        val pokemonEvolutionChain: List<PokeData>
    )

    private fun processEvolutionChain(pokemonEvolutionChain: EvolutionChain) =
        listOf(pokemonEvolutionChain.species) + evolution(pokemonEvolutionChain.evolvesTo)


    private fun evolution(evolutions: List<Evolution>): List<PokeData>{
        return if( evolutions.isEmpty() )
            evolutions.map { it.species }
        else {
            val list = evolutions.map { evolution(it.evolvesTo) }
            evolutions.map { it.species } + list.flatten()
        }
    }

}