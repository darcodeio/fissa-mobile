package com.fissa.kotlin.data.repository

import com.fissa.kotlin.data.remote.RodaApi
import com.fissa.kotlin.domain.model.Place
import com.fissa.kotlin.domain.model.TripItinerary
import com.fissa.kotlin.domain.model.TripLeg
import javax.inject.Inject
import kotlin.math.roundToInt

interface TripRepository {
    suspend fun plan(from: Place, to: Place): List<TripItinerary>
}

class RodaTripRepository @Inject constructor(
    private val api: RodaApi,
    private val deviceRegistrar: DeviceRegistrar,
) : TripRepository {

    /** Phase 1 du client Kotlin : train + marche uniquement (voir CLAUDE.md). */
    private val mode = "RAIL,WALK"

    override suspend fun plan(from: Place, to: Place): List<TripItinerary> {
        val uuid = deviceRegistrar.uuid()
        val response = api.planTrip(from.lat, from.lon, from.name, to.lat, to.lon, to.name, mode, uuid)
        return response.itineraries.map { itinerary ->
            TripItinerary(
                durationSeconds = itinerary.duration.roundToInt(),
                legs = itinerary.legs.map { leg ->
                    TripLeg(
                        mode = leg.mode,
                        durationSeconds = leg.duration.roundToInt(),
                        distanceMeters = leg.distance.roundToInt(),
                        fromName = leg.from.name,
                        toName = leg.to.name,
                        routeShortName = leg.routeShortName,
                    )
                },
            )
        }
    }
}
