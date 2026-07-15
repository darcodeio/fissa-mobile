package com.fissa.kotlin.data.repository

import com.fissa.kotlin.data.remote.RodaApi
import com.fissa.kotlin.domain.model.DirectionDeparture
import com.fissa.kotlin.domain.model.NearbyStation
import javax.inject.Inject

interface StationsRepository {
    suspend fun getNearbyStations(latitude: Double, longitude: Double): List<NearbyStation>
}

class RodaStationsRepository @Inject constructor(
    private val api: RodaApi,
) : StationsRepository {

    override suspend fun getNearbyStations(latitude: Double, longitude: Double): List<NearbyStation> {
        return api.getNearbyDepartures(latitude, longitude).map { dto ->
            NearbyStation(
                id = dto.id,
                name = dto.name,
                distanceMeters = dto.distanceMeters,
                departures = dto.departures.map { DirectionDeparture(it.direction, it.nextDepartureAt) },
            )
        }
    }
}
