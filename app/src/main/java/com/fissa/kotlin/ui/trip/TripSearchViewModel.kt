package com.fissa.kotlin.ui.trip

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fissa.kotlin.R
import com.fissa.kotlin.data.repository.TripRepository
import com.fissa.kotlin.domain.model.Place
import com.fissa.kotlin.domain.model.TripItinerary
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TripUiState {
    data object Idle : TripUiState
    data object Loading : TripUiState
    data class Success(val itineraries: List<TripItinerary>) : TripUiState
    data class Error(val message: String) : TripUiState
}

@HiltViewModel
class TripSearchViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _departure = MutableStateFlow<Place?>(null)
    val departure: StateFlow<Place?> = _departure.asStateFlow()

    private val _arrival = MutableStateFlow<Place?>(null)
    val arrival: StateFlow<Place?> = _arrival.asStateFlow()

    private val _tripState = MutableStateFlow<TripUiState>(TripUiState.Idle)
    val tripState: StateFlow<TripUiState> = _tripState.asStateFlow()

    fun onDepartureSelected(place: Place) {
        _departure.value = place
    }

    fun onArrivalSelected(place: Place) {
        _arrival.value = place
    }

    fun swapLocations() {
        val from = _departure.value
        val to = _arrival.value
        _departure.value = to
        _arrival.value = from
    }

    fun submit() {
        val from = _departure.value ?: return
        val to = _arrival.value ?: return

        viewModelScope.launch {
            _tripState.value = TripUiState.Loading
            _tripState.value = try {
                TripUiState.Success(tripRepository.plan(from, to))
            } catch (e: Exception) {
                TripUiState.Error(e.message ?: context.getString(R.string.trip_error_unknown))
            }
        }
    }
}
