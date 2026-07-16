package com.fissa.kotlin.data.repository

import com.fissa.kotlin.data.local.DeviceIdProvider
import com.fissa.kotlin.data.remote.RodaApi
import com.fissa.kotlin.domain.model.Place
import javax.inject.Inject

interface FavoritesRepository {
    suspend fun list(): List<Place>
    suspend fun add(place: Place)
    suspend fun remove(place: Place)
}

class RodaFavoritesRepository @Inject constructor(
    private val api: RodaApi,
    private val deviceIdProvider: DeviceIdProvider,
    private val deviceRegistrar: DeviceRegistrar,
) : FavoritesRepository {

    override suspend fun list(): List<Place> {
        deviceRegistrar.ensureRegistered()
        return api.getFavorites(deviceIdProvider.uuid).map { Place(it.name, it.lat, it.lon) }
    }

    override suspend fun add(place: Place) {
        deviceRegistrar.ensureRegistered()
        api.addFavorite(deviceIdProvider.uuid, place.name, place.lat, place.lon)
    }

    override suspend fun remove(place: Place) {
        deviceRegistrar.ensureRegistered()
        api.removeFavorite(deviceIdProvider.uuid, place.lat, place.lon)
    }
}
