package com.fissa.kotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.fissa.kotlin.ui.home.HomeRoute
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

/** Position par défaut (gare de Tunis) utilisée si la localisation est indisponible ou refusée. */
private const val DEFAULT_LATITUDE = 36.7972
private const val DEFAULT_LONGITUDE = 10.1858

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var latitude by mutableStateOf(DEFAULT_LATITUDE)
            var longitude by mutableStateOf(DEFAULT_LONGITUDE)

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { granted ->
                if (granted) {
                    fetchLastLocation { lat, lon -> latitude = lat; longitude = lon }
                }
            }

            LaunchedEffect(Unit) {
                if (hasLocationPermission()) {
                    fetchLastLocation { lat, lon -> latitude = lat; longitude = lon }
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            MaterialTheme {
                Surface {
                    HomeRoute(latitude = latitude, longitude = longitude)
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun fetchLastLocation(onResult: (Double, Double) -> Unit) {
        if (!hasLocationPermission()) return
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onResult(location.latitude, location.longitude)
            }
        }
    }
}
