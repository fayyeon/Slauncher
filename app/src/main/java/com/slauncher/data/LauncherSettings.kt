package com.slauncher.data

enum class WallpaperMode {
    WALLPAPER, // Transparent activity window showing Android wallpaper
    SOLID      // Solid dark or light background
}

data class LauncherSettings(
    val wallpaperMode: WallpaperMode = WallpaperMode.SOLID,
    val darkenWallpaper: Boolean = true,
    val useMaterialExpressiveTheming: Boolean = true
)
