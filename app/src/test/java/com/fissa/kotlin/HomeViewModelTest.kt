package com.fissa.kotlin

import com.fissa.kotlin.data.repository.StationsRepository
import com.fissa.kotlin.domain.model.DirectionDeparture
import com.fissa.kotlin.domain.model.NearbyStation
import com.fissa.kotlin.ui.home.HomeUiState
import com.fissa.kotlin.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNearbyStations expose les gares en cas de succes`() = runTest {
        val fakeRepository = object : StationsRepository {
            override suspend fun getNearbyStations(latitude: Double, longitude: Double): List<NearbyStation> {
                return listOf(
                    NearbyStation(
                        id = "tunis-marine",
                        name = "Tunis Marine",
                        distanceMeters = 350,
                        departures = listOf(DirectionDeparture("Vers Borj Cedria", "10:15")),
                    ),
                )
            }
        }
        val viewModel = HomeViewModel(fakeRepository)

        viewModel.loadNearbyStations(36.8, 10.18)
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals(1, state.stations.size)
        assertEquals("Tunis Marine", state.stations.first().name)
    }
}
