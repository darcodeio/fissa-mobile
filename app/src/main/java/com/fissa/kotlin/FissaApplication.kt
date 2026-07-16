package com.fissa.kotlin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import java.io.File

@HiltAndroidApp
class FissaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidBasePath = File(cacheDir, "osmdroid")
            osmdroidTileCache = File(osmdroidBasePath, "tiles")
        }
    }
}
