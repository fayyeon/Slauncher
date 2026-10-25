package com.slauncher.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.slauncher.data.AppModel
import com.slauncher.data.LauncherSettings
import com.slauncher.data.SettingsRepository
import com.slauncher.data.WallpaperMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    val settings: StateFlow<LauncherSettings> = settingsRepository.settings

    private val _allApps = MutableStateFlow<List<AppModel>>(emptyList())
    val allApps: StateFlow<List<AppModel>> = _allApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Filtered apps based on real-time search query, strictly sorted alphabetically
    val filteredApps: StateFlow<List<AppModel>> = combine(_allApps, _searchQuery) { apps, query ->
        if (query.isBlank()) {
            apps
        } else {
            apps.filter { it.label.contains(query.trim(), ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadInstalledApps()
    }

    /**
     * Queries package manager for all launchable apps.
     * Excludes self package, and sorts alphabetically by label (case-insensitive).
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val appsList = withContext(Dispatchers.IO) {
                queryLaunchableApps(getApplication())
            }
            _allApps.value = appsList
            _isLoading.value = false
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun launchFirstMatch(context: Context) {
        val matches = filteredApps.value
        if (matches.isNotEmpty()) {
            matches.first().launch(context)
            clearSearch()
        }
    }

    fun setWallpaperMode(mode: WallpaperMode) {
        settingsRepository.updateWallpaperMode(mode)
    }

    fun setDarkenWallpaper(darken: Boolean) {
        settingsRepository.updateDarkenWallpaper(darken)
    }

    fun setExpressiveTheming(enabled: Boolean) {
        settingsRepository.updateExpressiveTheming(enabled)
    }

    private fun queryLaunchableApps(context: Context): List<AppModel> {
        val pm: PackageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = try {
            pm.queryIntentActivities(mainIntent, 0)
        } catch (_: Throwable) {
            emptyList()
        }

        val myPackageName = context.packageName

        // Generic Android icon fallback bitmap
        val genericDefaultBitmap: Bitmap? = try {
            val d = ContextCompat.getDrawable(context, android.R.drawable.sym_def_app_icon)
                ?: pm.defaultActivityIcon
            d?.toSafeBitmap()
        } catch (_: Throwable) {
            null
        }

        return resolveInfos
            .filter { resolveInfo ->
                // Filter out this launcher itself
                resolveInfo.activityInfo.packageName != myPackageName
            }
            .distinctBy { "${it.activityInfo.packageName}/${it.activityInfo.name}" }
            .map { resolveInfo ->
                val label = try {
                    resolveInfo.loadLabel(pm).toString()
                } catch (_: Throwable) {
                    resolveInfo.activityInfo.packageName
                }

                // Check if the app actually specifies its own logo
                val hasCustomLogo = resolveInfo.activityInfo.icon != 0 ||
                        (resolveInfo.activityInfo.applicationInfo != null && resolveInfo.activityInfo.applicationInfo.icon != 0)

                val iconBitmap: Bitmap? = if (hasCustomLogo) {
                    try {
                        resolveInfo.loadIcon(pm)?.toSafeBitmap() ?: genericDefaultBitmap
                    } catch (_: Throwable) {
                        genericDefaultBitmap
                    }
                } else {
                    genericDefaultBitmap
                }

                AppModel(
                    packageName = resolveInfo.activityInfo.packageName,
                    activityName = resolveInfo.activityInfo.name,
                    label = label,
                    icon = iconBitmap
                )
            }
            // Criterion 6: Alphabetical sorting (case-insensitive)
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
    }
}

/**
 * Safely converts an Android Drawable to an in-memory ARGB_8888 Bitmap.
 * Catches all Throwables (OOM, Hardware bitmap issues, invalid intrinsic bounds).
 */
private fun Drawable.toSafeBitmap(): Bitmap? {
    return try {
        if (this is BitmapDrawable && this.bitmap != null && !this.bitmap.isRecycled) {
            val bmp = this.bitmap
            if (bmp.config != Bitmap.Config.HARDWARE) {
                return bmp
            } else {
                return bmp.copy(Bitmap.Config.ARGB_8888, false)
            }
        }
        val targetSize = 96
        val bitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, targetSize, targetSize)
        draw(canvas)
        bitmap
    } catch (_: Throwable) {
        null
    }
}

