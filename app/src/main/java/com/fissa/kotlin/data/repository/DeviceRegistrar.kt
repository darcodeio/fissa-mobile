package com.fissa.kotlin.data.repository

import com.fissa.kotlin.data.local.DeviceIdProvider
import com.fissa.kotlin.data.remote.RodaApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Enregistre le device (POST /api/v1/devices) une seule fois par session, avant tout appel
 * nécessitant un device existant côté Roda (ex. /api/v1/favorites). */
@Singleton
class DeviceRegistrar @Inject constructor(
    private val api: RodaApi,
    private val deviceIdProvider: DeviceIdProvider,
) {
    private val mutex = Mutex()
    private var registered = false

    suspend fun ensureRegistered() {
        if (registered) return
        mutex.withLock {
            if (registered) return
            runCatching { api.registerDevice(deviceIdProvider.uuid) }.onSuccess { registered = true }
        }
    }
}
