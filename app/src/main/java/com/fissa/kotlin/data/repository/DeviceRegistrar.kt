package com.fissa.kotlin.data.repository

import com.fissa.kotlin.data.local.DeviceIdProvider
import com.fissa.kotlin.data.remote.RodaApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Source de vérité de l'uuid device pour toute l'appli — appelé au démarrage (voir
 * FissaApplication) puis avant chaque appel API nécessitant un uuid.
 *
 * Premier lancement : aucun uuid stocké localement, POST /api/v1/devices est appelé sans uuid,
 * Roda en génère un et le renvoie — c'est cette réponse qui est persistée localement. Lancements
 * suivants : l'uuid stocké est renvoyé à Roda tel quel (met à jour last_seen_at côté serveur).
 * Le résultat est mémorisé en mémoire pour la durée du process, un seul appel réseau par session. */
@Singleton
class DeviceRegistrar @Inject constructor(
    private val api: RodaApi,
    private val deviceIdProvider: DeviceIdProvider,
) {
    private val mutex = Mutex()
    private var resolvedUuid: String? = null

    /** Retourne l'uuid, ou null si Roda est injoignable (l'appelant doit alors composer sans
     * uuid — tous les endpoints le traitent comme optionnel côté Roda). Un prochain appel
     * retentera l'enregistrement tant qu'aucun uuid n'a été obtenu. */
    suspend fun uuid(): String? {
        resolvedUuid?.let { return it }
        mutex.withLock {
            resolvedUuid?.let { return it }
            return runCatching { api.registerDevice(deviceIdProvider.uuid) }
                .onSuccess { deviceIdProvider.uuid = it.uuid }
                .getOrNull()
                ?.uuid
                ?.also { resolvedUuid = it }
        }
    }
}
