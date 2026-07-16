package com.fissa.kotlin.ui.trip

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fissa.kotlin.R
import com.fissa.kotlin.domain.model.Place
import com.fissa.kotlin.domain.model.TripItinerary
import com.fissa.kotlin.domain.model.TripLeg

@Composable
fun TripSearchRoute(
    viewModel: TripSearchViewModel = hiltViewModel(),
    onDepartureFieldClick: () -> Unit,
    onArrivalFieldClick: () -> Unit,
) {
    val departure by viewModel.departure.collectAsState()
    val arrival by viewModel.arrival.collectAsState()
    val tripState by viewModel.tripState.collectAsState()

    TripSearchScreen(
        departure = departure,
        arrival = arrival,
        tripState = tripState,
        onDepartureFieldClick = onDepartureFieldClick,
        onArrivalFieldClick = onArrivalFieldClick,
        onSubmit = viewModel::submit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSearchScreen(
    departure: Place?,
    arrival: Place?,
    tripState: TripUiState,
    onDepartureFieldClick: () -> Unit,
    onArrivalFieldClick: () -> Unit,
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
            LocationField(
                label = stringResource(R.string.trip_departure_label),
                place = departure,
                onClick = onDepartureFieldClick,
            )
            LocationField(
                label = stringResource(R.string.trip_arrival_label),
                place = arrival,
                onClick = onArrivalFieldClick,
            )

            val submitInteractionSource = remember { MutableInteractionSource() }
            val isSubmitPressed by submitInteractionSource.collectIsPressedAsState()
            val submitScale by animateFloatAsState(
                targetValue = if (isSubmitPressed) 0.96f else 1f,
                animationSpec = tween(100),
                label = "submitButtonScale",
            )
            Button(
                onClick = onSubmit,
                enabled = departure != null && arrival != null,
                interactionSource = submitInteractionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(submitScale),
            ) {
                Text(stringResource(R.string.trip_submit))
            }
            TripResults(tripState)
        }
    }
}

/** Champ en lecture seule : cliquer dessus ouvre l'écran de choix du lieu (autocomplete, GPS,
 * carte ou favori) plutôt que de saisir directement le texte ici. */
@Composable
private fun LocationField(label: String, place: Place?, onClick: () -> Unit) {
    OutlinedTextField(
        value = place?.name.orEmpty(),
        onValueChange = {},
        readOnly = true,
        enabled = false,
        label = { Text(label) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    )
}

@Composable
private fun TripResults(tripState: TripUiState) {
    AnimatedContent(
        targetState = tripState,
        transitionSpec = {
            (fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 6 }) togetherWith
                (fadeOut(tween(120)) + slideOutVertically(tween(120)) { -it / 6 })
        },
        label = "tripResults",
    ) { state ->
        when (state) {
            is TripUiState.Idle -> Text(stringResource(R.string.trip_empty_state))
            is TripUiState.Loading -> CircularProgressIndicator()
            is TripUiState.Error -> Text(state.message)
            is TripUiState.Success -> ItineraryList(state.itineraries)
        }
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
