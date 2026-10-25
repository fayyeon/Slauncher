package com.slauncher.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _settings = MutableStateFlow(readSettings())
    val settings: StateFlow<LauncherSettings> = _settings.asStateFlow()

    private fun readSettings(): LauncherSettings {
        val modeStr = prefs.getString(KEY_WALLPAPER_MODE, WallpaperMode.SOLID.name)
        val mode = try {
            WallpaperMode.valueOf(modeStr ?: WallpaperMode.SOLID.name)
        } catch (_: Exception) {
            WallpaperMode.SOLID
        }
        val darken = prefs.getBoolean(KEY_DARKEN_WALLPAPER, true)
        val expressive = prefs.getBoolean(KEY_EXPRESSIVE_THEMING, true)

        return LauncherSettings(
            wallpaperMode = mode,
            darkenWallpaper = darken,
            useMaterialExpressiveTheming = expressive
        )
    }

    fun updateWallpaperMode(mode: WallpaperMode) {
        prefs.edit().putString(KEY_WALLPAPER_MODE, mode.name).apply()
        _settings.value = _settings.value.copy(wallpaperMode = mode)
    }

    fun updateDarkenWallpaper(darken: Boolean) {
        prefs.edit().putBoolean(KEY_DARKEN_WALLPAPER, darken).apply()
        _settings.value = _settings.value.copy(darkenWallpaper = darken)
    }

    fun updateExpressiveTheming(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_EXPRESSIVE_THEMING, enabled).apply()
        _settings.value = _settings.value.copy(useMaterialExpressiveTheming = enabled)
    }

    companion object {
        private const val PREFS_NAME = "slauncher_prefs"
        private const val KEY_WALLPAPER_MODE = "wallpaper_mode"
        private const val KEY_DARKEN_WALLPAPER = "darken_wallpaper"
        private const val KEY_EXPRESSIVE_THEMING = "expressive_theming"
    }
}
