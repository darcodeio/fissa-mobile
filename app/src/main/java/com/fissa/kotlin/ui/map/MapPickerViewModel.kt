package com.fissa.kotlin.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fissa.kotlin.R
import com.fissa.kotlin.data.repository.PlacesRepository
import com.fissa.kotlin.domain.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapPickerUiState(
    val loading: Boolean = false,
    val confirmedPlace: Place? = null,
)

@HiltViewModel
class MapPickerViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapPickerUiState())
    val uiState: StateFlow<MapPickerUiState> = _uiState.asStateFlow()

    fun confirmSelection(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            val fallbackName = context.getString(R.string.map_picker_selected_fallback_name)
            val place = placesRepository.reverseGeocode(lat, lon, fallbackName)
            _uiState.value = _uiState.value.copy(loading = false, confirmedPlace = place)
        }
    }
}
