package com.fissa.kotlin

import android.app.Application
import com.fissa.kotlin.data.repository.DeviceRegistrar
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import java.io.File

@HiltAndroidApp
class FissaApplication : Application() {

    @Inject lateinit var deviceRegistrar: DeviceRegistrar

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidBasePath = File(cacheDir, "osmdroid")
            osmdroidTileCache = File(osmdroidBasePath, "tiles")
        }

        // Récupère (1er lancement) ou confirme (lancements suivants) l'uuid device auprès de
        // Roda dès le démarrage, pour qu'il soit déjà disponible en mémoire au premier appel API.
        applicationScope.launch { deviceRegistrar.uuid() }
    }
}
