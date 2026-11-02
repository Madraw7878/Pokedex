package com.example.pokedex

import com.example.pokedex.data.PokemonItem
import com.example.pokedex.data.PokemonRep
import com.example.pokedex.ui.ListContent
import com.example.pokedex.ui.ListEvent
import com.example.pokedex.ui.ListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {

    private val pikachu = PokemonItem(25, "pikachu", "url")
    private val bulbasaur = PokemonItem(1, "bulbasaur", "url")
    private val repo = mockk<PokemonRep>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_success_showsList() {
        coEvery { repo.getAll() } returns listOf(pikachu, bulbasaur)
        val vm = ListViewModel(repo)
        assertEquals(ListContent.Success(listOf(pikachu, bulbasaur)), vm.state.value.content)
    }

    @Test
    fun load_apiError_showsError() {
        coEvery { repo.getAll() } throws IOException("Нет сети")
        val vm = ListViewModel(repo)
        assertTrue(vm.state.value.content is ListContent.Error)
    }

    @Test
    fun retry_afterError_showsList() {
        coEvery { repo.getAll() } throws IOException("Нет сети") andThen listOf(pikachu)
        val vm = ListViewModel(repo)
        assertTrue(vm.state.value.content is ListContent.Error)
        vm.onEvent(ListEvent.Retry)
        assertEquals(ListContent.Success(listOf(pikachu)), vm.state.value.content)
    }

    @Test
    fun search_filtersByName() {
        coEvery { repo.getAll() } returns listOf(pikachu, bulbasaur)
        val vm = ListViewModel(repo)
        vm.onEvent(ListEvent.QueryChanged("pika"))
        assertEquals(ListContent.Success(listOf(pikachu)), vm.state.value.content)
    }

    @Test
    fun search_noMatches_showsEmpty() {
        coEvery { repo.getAll() } returns listOf(pikachu, bulbasaur)
        val vm = ListViewModel(repo)
        vm.onEvent(ListEvent.QueryChanged("zzz"))
        assertEquals(ListContent.Empty, vm.state.value.content)
    }

    @Test
    fun typingWhileLoading_doesNotShowEmpty() {
        val gate = CompletableDeferred<List<PokemonItem>>()
        coEvery { repo.getAll() } coAnswers { gate.await() }
        val vm = ListViewModel(repo)

        vm.onEvent(ListEvent.QueryChanged("pika"))
        assertEquals(ListContent.Loading, vm.state.value.content)

        gate.complete(listOf(pikachu, bulbasaur))
        assertEquals(ListContent.Success(listOf(pikachu)), vm.state.value.content)
    }
}