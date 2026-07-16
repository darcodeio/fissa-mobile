package com.fissa.kotlin.data.repository

import com.fissa.kotlin.data.remote.RodaApi
import com.fissa.kotlin.domain.model.Place
import javax.inject.Inject

interface PlacesRepository {
    suspend fun search(query: String): List<Place>

    /** Reverse-geocoding (GPS, carte) — retourne toujours un Place, avec [fallbackName] si Photon
     * ne trouve pas d'adresse ou si l'appel échoue. */
    suspend fun reverseGeocode(lat: Double, lon: Double, fallbackName: String): Place
}

class RodaPlacesRepository @Inject constructor(
    private val api: RodaApi,
) : PlacesRepository {

    override suspend fun search(query: String): List<Place> {
        if (query.isBlank()) return emptyList()
        return api.searchPlaces(query).map { Place(it.name, it.lat, it.lon) }
    }

    override suspend fun reverseGeocode(lat: Double, lon: Double, fallbackName: String): Place {
        val name = runCatching { api.reverseGeocode(lat, lon).name }.getOrNull()
        return Place(name ?: fallbackName, lat, lon)
    }
}
