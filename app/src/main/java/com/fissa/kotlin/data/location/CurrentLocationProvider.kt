package com.fissa.kotlin.data.location

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class CurrentLocationProvider @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    /** Retourne null si la permission n'est pas accordée ou si la position est indisponible. */
    suspend fun getCurrentLocation(): Location? {
        return try {
            client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token).await()
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
        }
    }
}
