package com.example.pokedex.data

class PokemonRep(private val api: PokeApi = Network.api) {
    suspend fun getAll(): List<PokemonItem> =
        api.getPokemonList(limit = 151, offset = 0).results.map { dto ->
            // url вида https://pokeapi.co/api/v2/pokemon/25/ -> id = 25
            val id = dto.url.trimEnd('/').substringAfterLast('/').toInt()
            PokemonItem(id = id, name = dto.name, imageUrl = imageUrlFor(id))
        }

    suspend fun getById(id: Int): PokemonDetailDto = api.getPokemon(id)
}
