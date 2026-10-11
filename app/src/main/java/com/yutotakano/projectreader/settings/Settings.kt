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
import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.abs


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
        context.dataStore.edit { preferences ->
            // https://stackoverflow.com/questions/39118528/rgb-to-hsl-conversion
            val max = max(listOf(color.red, color.blue, color.green))
            val min = min(listOf(color.red, color.blue, color.green))
            var hue = 0f;
            var sat = 0f;
            var lum = (max + min) / 2f;

            if (max != min) {
                val chroma = max - min;
                sat = chroma / (1f - abs(2f * lum - 1));
                when(max) {
                    color.red -> {
                        val segment = (color.green - color.blue) / chroma
                        var shift = 0f / 60;
                        if (segment < 0) {
                            shift = 360f / 60;
                        }
                        hue = segment + shift;
                    }
                    color.green -> {
                        val segment = (color.blue - color.red) / chroma
                        val shift = 120f / 60;
                        hue = segment + shift;
                    }
                    color.blue -> {
                        val segment = (color.red - color.green) / chroma;
                        val shift = 240f / 60;
                        hue = segment + shift;
                    }
                }
            }

            hue *= 60;
            sat *= 100;
            lum *= 100;

            preferences.set(APPEARANCE_COLOR_HUE_SETTING_KEY, hue);
            preferences.set(APPEARANCE_COLOR_SATURATION_SETTING_KEY, sat);
            preferences.set(APPEARANCE_COLOR_LUMINOSITY_SETTING_KEY, lum);
        }
    }


    companion object {
        val APPEARANCE_COLOR_HUE_SETTING_KEY = floatPreferencesKey("appearance.color.hue")
        val APPEARANCE_COLOR_SATURATION_SETTING_KEY = floatPreferencesKey("appearance.color.saturation")
        val APPEARANCE_COLOR_LUMINOSITY_SETTING_KEY = floatPreferencesKey("appearance.color.luminosity")
    }
}