package com.slauncher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slauncher.R
import com.slauncher.data.WallpaperMode
import com.slauncher.service.LockScreenService
import com.slauncher.ui.components.AppListItem
import com.slauncher.ui.components.LauncherBottomBar
import com.slauncher.ui.components.SettingsBottomSheet
import com.slauncher.ui.theme.WallpaperDarkOverlay
import com.slauncher.ui.theme.WallpaperLightOverlay

/**
 * Main Launcher Screen adhering strictly to all user criteria:
 * 1. Scrollable minimalist app list.
 * 2. 3-dots hamburger menu to change background (transparent wallpaper / darken / solid B&W or expressive).
 * 3. Registered as home launcher only without launchable icon.
 * 4. Rounded search bar matching concept image.
 * 5. Zero animation code (lightweight, instantaneous transitions).
 * 6. Alphabetically sorted apps.
 * 7. System font exclusively.
 * 8. Samsung One UI reachability (list starts at middle of screen and scrolls up naturally).
 */
@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showSettingsSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Scroll to top when search query updates
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    // Handle back button: dismiss search or close settings
    BackHandler(enabled = searchQuery.isNotEmpty() || showSettingsSheet) {
        if (showSettingsSheet) {
            showSettingsSheet = false
        } else if (searchQuery.isNotEmpty()) {
            viewModel.clearSearch()
        }
    }

    // Determine root background color
    val rootBackgroundColor: Color = when (settings.wallpaperMode) {
        WallpaperMode.WALLPAPER -> Color.Transparent
        WallpaperMode.SOLID -> MaterialTheme.colorScheme.background
    }

    val isDark = isSystemInDarkTheme()
    val overlayColor = if (isDark) WallpaperDarkOverlay else WallpaperLightOverlay

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(rootBackgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        LockScreenService.lockScreen(context)
                    }
                )
            }
    ) {
        // Wallpaper darken/lighten scrim overlay if wallpaper mode is active & darken is enabled
        if (settings.wallpaperMode == WallpaperMode.WALLPAPER && settings.darkenWallpaper) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(overlayColor)
            )
        }

        // Screen layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Main content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else if (filteredApps.isEmpty() && searchQuery.isNotEmpty()) {
                    // Empty search result state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_apps_found),
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                            fontFamily = FontFamily.Default,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    val isSearching = searchQuery.isNotEmpty()
                    val configuration = LocalConfiguration.current
                    val screenHeightDp = configuration.screenHeightDp.dp

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = if (isSearching) Arrangement.Bottom else Arrangement.Top,
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        if (!isSearching) {
                            // Samsung One UI top reachability spacer: apps start at ~42% down the screen
                            item(key = "reachability_spacer") {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(screenHeightDp * 0.42f)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onDoubleTap = {
                                                    LockScreenService.lockScreen(context)
                                                }
                                            )
                                        }
                                )
                            }
                        }

                        // App list items strictly sorted alphabetically
                        items(
                            items = filteredApps,
                            key = { "${it.packageName}/${it.activityName}" }
                        ) { app ->
                            AppListItem(app = app)
                        }
                    }
                }
            }

            // Bottom Navigation and Search Bar (matching concept screenshot)
            LauncherBottomBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onSearchAction = {
                    viewModel.launchFirstMatch(context)
                },
                onOptionsClick = {
                    showSettingsSheet = true
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }

        // Settings BottomSheet modal
        if (showSettingsSheet) {
            SettingsBottomSheet(
                settings = settings,
                onWallpaperModeChange = viewModel::setWallpaperMode,
                onDarkenWallpaperChange = viewModel::setDarkenWallpaper,
                onExpressiveThemingChange = viewModel::setExpressiveTheming,
                onDismissRequest = { showSettingsSheet = false }
            )
        }
    }
}
