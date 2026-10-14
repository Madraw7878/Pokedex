package com.example.pokedex.data

import com.google.gson.annotations.SerializedName

data class PokemonListResponse(val results: List<PokemonListItemDto>)
data class PokemonListItemDto(val name: String, val url: String)

data class NamedRef(val name: String)
data class TypeSlot(val type: NamedRef)
data class StatSlot(@SerializedName("base_stat") val baseStat: Int, val stat: NamedRef)
data class AbilitySlot(val ability: NamedRef)

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeSlot>,
    val stats: List<StatSlot>,
    val abilities: List<AbilitySlot>
)

data class PokemonItem(val id: Int, val name: String, val imageUrl: String)

fun imageUrlFor(id: Int) =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
