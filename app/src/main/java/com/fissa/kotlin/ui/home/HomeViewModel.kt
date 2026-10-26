package com.fissa.kotlin.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fissa.kotlin.R
import com.fissa.kotlin.data.repository.StationsRepository
import com.fissa.kotlin.domain.model.NearbyStation
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val stations: List<NearbyStation>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stationsRepository: StationsRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadNearbyStations(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            _uiState.value = try {
                HomeUiState.Success(stationsRepository.getNearbyStations(latitude, longitude))
            } catch (e: Exception) {
                HomeUiState.Error(e.message ?: context.getString(R.string.trip_error_unknown))
            }
        }
    }
}
