package com.fissa.kotlin.data.remote

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RodaApi {

    /** Écran d'accueil "gares proches" — implémenté côté fissa_roda, actuellement non branché
     * dans l'UI (voir fissa_kotlin/CLAUDE.md, reporté en phase 2 au profit de la recherche
     * d'itinéraire par saisie départ/arrivée). */
    @GET("api/v1/stations/nearby-departures")
    suspend fun getNearbyDepartures(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): List<NearbyStationDto>

    /** Autocomplete d'adresses — proxy Photon côté fissa_roda. */
    @GET("api/v1/places")
    suspend fun searchPlaces(@Query("q") query: String): List<PlaceDto>

    /** Recherche d'itinéraire — proxy OTP2 côté fissa_roda. */
    @FormUrlEncoded
    @POST("api/v1/trip")
    suspend fun planTrip(
        @Field("from_lat") fromLat: Double,
        @Field("from_lon") fromLon: Double,
        @Field("to_lat") toLat: Double,
        @Field("to_lon") toLon: Double,
        @Field("mode") mode: String,
    ): TripResponseDto
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

data class PlaceDto(
    val name: String,
    val lat: Double,
    val lon: Double,
)

data class TripResponseDto(
    val itineraries: List<ItineraryDto>,
)

data class ItineraryDto(
    val duration: Double,
    val legs: List<LegDto>,
)

data class LegDto(
    val mode: String,
    val duration: Double,
    val distance: Double,
    val from: LegPointDto,
    val to: LegPointDto,
    val routeShortName: String?,
)

data class LegPointDto(
    val name: String?,
    val lat: Double,
    val lon: Double,
)
