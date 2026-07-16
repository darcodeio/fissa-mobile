package com.fissa.kotlin.ui.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.fissa.kotlin.R
import com.fissa.kotlin.domain.model.Place

@Composable
fun LocationPickerRoute(
    field: LocationField,
    viewModel: LocationPickerViewModel = hiltViewModel(),
    onPlaceSelected: (Place) -> Unit,
    onMapPickerRequested: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.selectedPlace) {
        uiState.selectedPlace?.let(onPlaceSelected)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> if (granted) viewModel.useCurrentLocation() }

    LocationPickerScreen(
        field = field,
        uiState = uiState,
        onQueryChanged = viewModel::onQueryChanged,
        onPlaceClick = viewModel::selectPlace,
        onCurrentLocationClick = {
            if (hasLocationPermission(context)) {
                viewModel.useCurrentLocation()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        },
        onMapPickerClick = onMapPickerRequested,
        onBack = onBack,
    )
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    field: LocationField,
    uiState: LocationPickerUiState,
    onQueryChanged: (String) -> Unit,
    onPlaceClick: (Place) -> Unit,
    onCurrentLocationClick: () -> Unit,
    onMapPickerClick: () -> Unit,
    onBack: () -> Unit,
) {
    val title = stringResource(
        if (field == LocationField.DEPARTURE) R.string.location_picker_departure_title else R.string.location_picker_arrival_title,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChanged,
                label = { Text(stringResource(R.string.location_picker_search_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            AnimatedVisibility(visible = uiState.suggestions.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        uiState.suggestions.forEach { place ->
                            Text(
                                text = place.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPlaceClick(place) }
                                    .padding(12.dp),
                            )
                        }
                    }
                }
            }

            OptionRow(
                icon = Icons.Default.MyLocation,
                label = stringResource(R.string.location_picker_current_location),
                loading = uiState.gpsLoading,
                onClick = onCurrentLocationClick,
            )
            OptionRow(
                icon = Icons.Default.Map,
                label = stringResource(R.string.location_picker_choose_on_map),
                onClick = onMapPickerClick,
            )

            uiState.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

            Text(text = stringResource(R.string.location_picker_favorites_title), style = MaterialTheme.typography.titleMedium)
            when {
                uiState.favoritesLoading -> CircularProgressIndicator()
                uiState.favorites.isEmpty() -> Text(stringResource(R.string.location_picker_favorites_empty))
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(uiState.favorites) { place ->
                        FavoriteRow(place = place, onClick = { onPlaceClick(place) })
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionRow(icon: ImageVector, label: String, loading: Boolean = false, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
        Text(text = label, modifier = Modifier.weight(1f))
        if (loading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun FavoriteRow(place: Place, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
    ) {
        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
        Text(text = place.name)
    }
}
