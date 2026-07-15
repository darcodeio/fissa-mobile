package com.fissa.kotlin.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/** Endpoint dédié décrit dans CLAUDE.md — à créer côté fissa_roda. */
interface RodaApi {

    @GET("api/v1/stations/nearby-departures")
    suspend fun getNearbyDepartures(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): List<NearbyStationDto>
}

data class NearbyStationDto(
    val id: String,
    val name: String,
    val distanceMeters: Int,
    val departures: List<DirectionDepartureDto>,
)

data class DirectionDepartureDto(
    val direction: String,
    val nextDepartureAt: String?,
)
