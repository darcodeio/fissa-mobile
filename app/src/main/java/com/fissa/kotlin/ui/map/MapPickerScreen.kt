package com.fissa.kotlin.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.fissa.kotlin.R
import com.fissa.kotlin.domain.model.Place
import org.osmdroid.api.IGeoPoint
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

private const val DEFAULT_LAT = 36.8065
private const val DEFAULT_LON = 10.1815
private const val DEFAULT_ZOOM = 15.0
private const val PIN_ICON_SIZE_DP = 40

@Composable
fun MapPickerRoute(
    viewModel: MapPickerViewModel = hiltViewModel(),
    onPlaceConfirmed: (Place) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.confirmedPlace) {
        uiState.confirmedPlace?.let(onPlaceConfirmed)
    }

    MapPickerScreen(
        loading = uiState.loading,
        onConfirm = viewModel::confirmSelection,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(loading: Boolean, onConfirm: (lat: Double, lon: Double) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    var center by remember { mutableStateOf<IGeoPoint>(GeoPoint(DEFAULT_LAT, DEFAULT_LON)) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true) // pincer pour zoomer
            controller.setZoom(DEFAULT_ZOOM)
            controller.setCenter(GeoPoint(DEFAULT_LAT, DEFAULT_LON))
            addMapListener(object : MapListener {
                override fun onScroll(event: ScrollEvent?): Boolean {
                    event?.source?.mapCenter?.let { center = it }
                    return true
                }

                override fun onZoom(event: ZoomEvent?): Boolean = false
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map_picker_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

            // La pointe de l'icône (bas du glyphe), pas son centre, doit tomber exactement au
            // centre de l'écran = mapView.mapCenter = coordonnée réellement envoyée au clic sur
            // "Confirmer". D'où le décalage vers le haut de la moitié de la hauteur de l'icône.
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = -(PIN_ICON_SIZE_DP / 2).dp)
                    .size(PIN_ICON_SIZE_DP.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp, bottom = 96.dp),
            ) {
                FloatingActionButton(onClick = { mapView.controller.zoomIn() }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.map_picker_zoom_in))
                }
                FloatingActionButton(onClick = { mapView.controller.zoomOut() }) {
                    Icon(Icons.Default.Remove, contentDescription = stringResource(R.string.map_picker_zoom_out))
                }
            }

            Button(
                onClick = { onConfirm(center.latitude, center.longitude) },
                enabled = !loading,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(R.string.map_picker_confirm))
                }
            }
        }
    }
}
