package com.fissa.kotlin.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "fissa_device"
private const val KEY_UUID = "device_uuid"

/** Stockage local de l'uuid — généré côté Roda (voir DeviceRegistrar), pas ici. */
@Singleton
class DeviceIdProvider @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var uuid: String?
        get() = prefs.getString(KEY_UUID, null)
        set(value) = prefs.edit().putString(KEY_UUID, value).apply()
}
