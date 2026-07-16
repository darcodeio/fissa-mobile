package com.fissa.kotlin.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "fissa_device"
private const val KEY_UUID = "device_uuid"

/** UUID généré au premier lancement et persisté localement — voir CLAUDE.md racine. */
@Singleton
class DeviceIdProvider @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val uuid: String by lazy {
        prefs.getString(KEY_UUID, null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString(KEY_UUID, it).apply()
        }
    }
}
