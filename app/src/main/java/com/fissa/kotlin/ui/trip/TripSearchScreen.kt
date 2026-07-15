package com.fissa.kotlin.ui.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fissa.kotlin.R
import com.fissa.kotlin.domain.model.Place
import com.fissa.kotlin.domain.model.TripItinerary
import com.fissa.kotlin.domain.model.TripLeg

@Composable
fun TripSearchRoute(viewModel: TripSearchViewModel = hiltViewModel()) {
    val departure by viewModel.departure.collectAsState()
    val arrival by viewModel.arrival.collectAsState()
    val tripState by viewModel.tripState.collectAsState()

    TripSearchScreen(
        departure = departure,
        arrival = arrival,
        tripState = tripState,
        onDepartureQueryChanged = viewModel::onDepartureQueryChanged,
        onDepartureSelected = viewModel::onDepartureSelected,
        onArrivalQueryChanged = viewModel::onArrivalQueryChanged,
        onArrivalSelected = viewModel::onArrivalSelected,
        onSubmit = viewModel::submit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSearchScreen(
    departure: PlaceFieldState,
    arrival: PlaceFieldState,
    tripState: TripUiState,
    onDepartureQueryChanged: (String) -> Unit,
    onDepartureSelected: (Place) -> Unit,
    onArrivalQueryChanged: (String) -> Unit,
    onArrivalSelected: (Place) -> Unit,
    onSubmit: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.trip_search_title)) }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PlaceField(
                label = stringResource(R.string.trip_departure_label),
                state = departure,
                onQueryChanged = onDepartureQueryChanged,
                onSelected = onDepartureSelected,
            )
            PlaceField(
                label = stringResource(R.string.trip_arrival_label),
                state = arrival,
                onQueryChanged = onArrivalQueryChanged,
                onSelected = onArrivalSelected,
            )
            Button(
                onClick = onSubmit,
                enabled = departure.selected != null && arrival.selected != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.trip_submit))
            }
            TripResults(tripState)
        }
    }
}

@Composable
private fun PlaceField(
    label: String,
    state: PlaceFieldState,
    onQueryChanged: (String) -> Unit,
    onSelected: (Place) -> Unit,
) {
    Column {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChanged,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        if (state.selected == null && state.suggestions.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    state.suggestions.forEach { place ->
                        Text(
                            text = place.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelected(place) }
                                .padding(12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TripResults(tripState: TripUiState) {
    when (tripState) {
        is TripUiState.Idle -> Text(stringResource(R.string.trip_empty_state))
        is TripUiState.Loading -> CircularProgressIndicator()
        is TripUiState.Error -> Text(tripState.message)
        is TripUiState.Success -> ItineraryList(tripState.itineraries)
    }
}

@Composable
private fun ItineraryList(itineraries: List<TripItinerary>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(itineraries) { itinerary -> ItineraryCard(itinerary) }
    }
}

@Composable
private fun ItineraryCard(itinerary: TripItinerary) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = formatDuration(itinerary.durationSeconds), style = MaterialTheme.typography.titleMedium)
            itinerary.legs.forEach { leg -> Text(text = formatLeg(leg), style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

private fun formatLeg(leg: TripLeg): String {
    val label = leg.routeShortName?.let { "${leg.mode} $it" } ?: leg.mode
    return "$label — ${leg.fromName ?: "?"} → ${leg.toName ?: "?"} (${leg.distanceMeters} m, ${formatDuration(leg.durationSeconds)})"
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    return if (minutes < 60) "$minutes min" else "${minutes / 60}h${(minutes % 60).toString().padStart(2, '0')}"
}
