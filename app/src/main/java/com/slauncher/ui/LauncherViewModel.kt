package com.slauncher.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
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
        } catch (_: Exception) {
            emptyList()
        }

        val myPackageName = context.packageName

        return resolveInfos
            .filter { resolveInfo ->
                // Filter out this launcher itself
                resolveInfo.activityInfo.packageName != myPackageName
            }
            .map { resolveInfo ->
                val label = try {
                    resolveInfo.loadLabel(pm).toString()
                } catch (_: Exception) {
                    resolveInfo.activityInfo.packageName
                }

                val icon = try {
                    resolveInfo.loadIcon(pm)
                } catch (_: Exception) {
                    null
                }

                AppModel(
                    packageName = resolveInfo.activityInfo.packageName,
                    activityName = resolveInfo.activityInfo.name,
                    label = label,
                    icon = icon
                )
            }
            // Criterion 6: Alphabetical sorting (case-insensitive)
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
    }
}
