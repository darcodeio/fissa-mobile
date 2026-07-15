package com.fissa.kotlin.domain.model

data class TripItinerary(
    val durationSeconds: Int,
    val legs: List<TripLeg>,
)

data class TripLeg(
    val mode: String,
    val durationSeconds: Int,
    val distanceMeters: Int,
    val fromName: String?,
    val toName: String?,
    val routeShortName: String?,
)
