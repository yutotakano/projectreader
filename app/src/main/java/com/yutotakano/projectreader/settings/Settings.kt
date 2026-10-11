package com.yutotakano.projectreader.settings

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProjectReaderSettings(val context: Context) {
    fun appearanceColorFlow() =
        context.dataStore.data.map {
            preferences ->
            Color.hsl(
                preferences.get(APPEARANCE_COLOR_HUE_SETTING_KEY) ?: 1f,
                preferences.get(APPEARANCE_COLOR_SATURATION_SETTING_KEY) ?: 0f,
                preferences.get(APPEARANCE_COLOR_LUMINOSITY_SETTING_KEY) ?: 0f
            )
        }

    suspend fun updateAppearanceColor(color: Color) {
        context.dataStore.edit { preferences -> preferences }
    }


    companion object {
        val APPEARANCE_COLOR_HUE_SETTING_KEY = floatPreferencesKey("appearance.color.hue")
        val APPEARANCE_COLOR_SATURATION_SETTING_KEY = floatPreferencesKey("appearance.color.saturation")
        val APPEARANCE_COLOR_LUMINOSITY_SETTING_KEY = floatPreferencesKey("appearance.color.luminosity")
    }
}