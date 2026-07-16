package com.fissa.kotlin.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fissa.kotlin.data.location.CurrentLocationProvider
import com.fissa.kotlin.data.repository.FavoritesRepository
import com.fissa.kotlin.data.repository.PlacesRepository
import com.fissa.kotlin.domain.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val SEARCH_DEBOUNCE_MS = 300L
private const val CURRENT_LOCATION_NAME = "Ma position actuelle"

data class LocationPickerUiState(
    val query: String = "",
    val suggestions: List<Place> = emptyList(),
    val favorites: List<Place> = emptyList(),
    val favoritesLoading: Boolean = true,
    val gpsLoading: Boolean = false,
    val error: String? = null,
    val selectedPlace: Place? = null,
)

@HiltViewModel
class LocationPickerViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val favoritesRepository: FavoritesRepository,
    private val currentLocationProvider: CurrentLocationProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationPickerUiState())
    val uiState: StateFlow<LocationPickerUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadFavorites()
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            val results = runCatching { placesRepository.search(query) }.getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(suggestions = results)
        }
    }

    fun selectPlace(place: Place) {
        _uiState.value = _uiState.value.copy(selectedPlace = place)
    }

    /** Suppose que l'appelant a déjà vérifié/obtenu la permission de localisation. */
    fun useCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(gpsLoading = true, error = null)
            val location = currentLocationProvider.getCurrentLocation()
            _uiState.value = if (location != null) {
                val place = placesRepository.reverseGeocode(location.latitude, location.longitude, CURRENT_LOCATION_NAME)
                _uiState.value.copy(gpsLoading = false, selectedPlace = place)
            } else {
                _uiState.value.copy(gpsLoading = false, error = "Position indisponible")
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(favoritesLoading = true)
            val favorites = runCatching { favoritesRepository.list() }.getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(favorites = favorites, favoritesLoading = false)
        }
    }
}
