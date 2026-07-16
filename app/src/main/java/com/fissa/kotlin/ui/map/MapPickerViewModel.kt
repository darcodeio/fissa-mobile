package com.fissa.kotlin.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fissa.kotlin.data.repository.PlacesRepository
import com.fissa.kotlin.domain.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MAP_SELECTED_FALLBACK_NAME = "Point sélectionné sur la carte"

data class MapPickerUiState(
    val loading: Boolean = false,
    val confirmedPlace: Place? = null,
)

@HiltViewModel
class MapPickerViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapPickerUiState())
    val uiState: StateFlow<MapPickerUiState> = _uiState.asStateFlow()

    fun confirmSelection(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            val place = placesRepository.reverseGeocode(lat, lon, MAP_SELECTED_FALLBACK_NAME)
            _uiState.value = _uiState.value.copy(loading = false, confirmedPlace = place)
        }
    }
}
