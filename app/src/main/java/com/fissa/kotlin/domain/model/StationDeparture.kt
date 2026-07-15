package com.fissa.kotlin.domain.model

data class NearbyStation(
    val id: String,
    val name: String,
    val distanceMeters: Int,
    val departures: List<DirectionDeparture>,
)

data class DirectionDeparture(
    val direction: String,
    val nextDepartureAt: String?,
)
