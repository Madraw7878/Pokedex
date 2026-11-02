package com.example.pokedex

import androidx.lifecycle.SavedStateHandle
import com.example.pokedex.data.FavouritesRep
import com.example.pokedex.data.PokemonDetailDto
import com.example.pokedex.data.PokemonRep
import com.example.pokedex.ui.DetailEvent
import com.example.pokedex.ui.DetailUiState
import com.example.pokedex.ui.DetailViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val dto = PokemonDetailDto(25, "pikachu", 4, 60, emptyList(), emptyList(), emptyList())
    private val repo = mockk<PokemonRep>()

    private fun createViewModel() =
        DetailViewModel(SavedStateHandle(mapOf("id" to 25)), repo)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        FavouritesRep.remove(25)
        Dispatchers.resetMain()
    }

    @Test
    fun load_success_showsDetails() {
        coEvery { repo.getById(25) } returns dto
        val vm = createViewModel()
        assertTrue(vm.state.value is DetailUiState.Success)
    }

    @Test
    fun load_error_showsError_thenRetryWorks() {
        coEvery { repo.getById(25) } throws IOException("Нет сети") andThen dto
        val vm = createViewModel()
        assertTrue(vm.state.value is DetailUiState.Error)
        vm.onEvent(DetailEvent.Retry)
        assertTrue(vm.state.value is DetailUiState.Success)
    }

    @Test
    fun toggleFavourite_changesFlag() {
        coEvery { repo.getById(25) } returns dto
        val vm = createViewModel()

        vm.onEvent(DetailEvent.ToggleFavourite)
        assertTrue((vm.state.value as DetailUiState.Success).isFavourite)

        vm.onEvent(DetailEvent.ToggleFavourite)
        assertFalse((vm.state.value as DetailUiState.Success).isFavourite)
    }
}