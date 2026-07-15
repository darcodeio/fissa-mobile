package com.fissa.kotlin.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fissa.kotlin.data.repository.PlacesRepository
import com.fissa.kotlin.data.repository.TripRepository
import com.fissa.kotlin.domain.model.Place
import com.fissa.kotlin.domain.model.TripItinerary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val SEARCH_DEBOUNCE_MS = 300L

data class PlaceFieldState(
    val query: String = "",
    val suggestions: List<Place> = emptyList(),
    val selected: Place? = null,
)

sealed interface TripUiState {
    data object Idle : TripUiState
    data object Loading : TripUiState
    data class Success(val itineraries: List<TripItinerary>) : TripUiState
    data class Error(val message: String) : TripUiState
}

@HiltViewModel
class TripSearchViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val tripRepository: TripRepository,
) : ViewModel() {

    private val _departure = MutableStateFlow(PlaceFieldState())
    val departure: StateFlow<PlaceFieldState> = _departure.asStateFlow()

    private val _arrival = MutableStateFlow(PlaceFieldState())
    val arrival: StateFlow<PlaceFieldState> = _arrival.asStateFlow()

    private val _tripState = MutableStateFlow<TripUiState>(TripUiState.Idle)
    val tripState: StateFlow<TripUiState> = _tripState.asStateFlow()

    private var departureSearchJob: Job? = null
    private var arrivalSearchJob: Job? = null

    fun onDepartureQueryChanged(query: String) {
        _departure.value = _departure.value.copy(query = query, selected = null)
        departureSearchJob = searchPlaces(query, departureSearchJob) { results ->
            _departure.value = _departure.value.copy(suggestions = results)
        }
    }

    fun onArrivalQueryChanged(query: String) {
        _arrival.value = _arrival.value.copy(query = query, selected = null)
        arrivalSearchJob = searchPlaces(query, arrivalSearchJob) { results ->
            _arrival.value = _arrival.value.copy(suggestions = results)
        }
    }

    fun onDepartureSelected(place: Place) {
        _departure.value = PlaceFieldState(query = place.name, selected = place)
    }

    fun onArrivalSelected(place: Place) {
        _arrival.value = PlaceFieldState(query = place.name, selected = place)
    }

    fun submit() {
        val from = _departure.value.selected ?: return
        val to = _arrival.value.selected ?: return

        viewModelScope.launch {
            _tripState.value = TripUiState.Loading
            _tripState.value = try {
                TripUiState.Success(tripRepository.plan(from, to))
            } catch (e: Exception) {
                TripUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    private fun searchPlaces(query: String, previousJob: Job?, onResult: (List<Place>) -> Unit): Job {
        previousJob?.cancel()
        return viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            onResult(runCatching { placesRepository.search(query) }.getOrDefault(emptyList()))
        }
    }
}
