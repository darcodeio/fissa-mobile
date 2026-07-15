package com.fissa.kotlin.data.repository

import com.fissa.kotlin.data.remote.RodaApi
import com.fissa.kotlin.domain.model.Place
import javax.inject.Inject

interface PlacesRepository {
    suspend fun search(query: String): List<Place>
}

class RodaPlacesRepository @Inject constructor(
    private val api: RodaApi,
) : PlacesRepository {

    override suspend fun search(query: String): List<Place> {
        if (query.isBlank()) return emptyList()
        return api.searchPlaces(query).map { Place(it.name, it.lat, it.lon) }
    }
}
