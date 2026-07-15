package com.fissa.kotlin.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fissa.kotlin.R
import com.fissa.kotlin.domain.model.NearbyStation

@Composable
fun HomeRoute(
    latitude: Double,
    longitude: Double,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(latitude, longitude) {
        viewModel.loadNearbyStations(latitude, longitude)
    }

    HomeScreen(uiState = uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(uiState: HomeUiState) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.home_title)) }) },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Error -> Text(
                    text = uiState.message,
                    modifier = Modifier.align(Alignment.Center),
                )
                is HomeUiState.Success -> NearbyStationsList(uiState.stations)
            }
        }
    }
}

@Composable
private fun NearbyStationsList(stations: List<NearbyStation>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(stations, key = { it.id }) { station ->
            StationCard(station)
        }
    }
}

@Composable
private fun StationCard(station: NearbyStation) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = station.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${station.distanceMeters} m",
                style = MaterialTheme.typography.bodySmall,
            )
            station.departures.forEach { departure ->
                Text(
                    text = "${departure.direction} — ${departure.nextDepartureAt ?: "aucun passage"}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
