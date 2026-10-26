package com.slauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slauncher.R
import com.slauncher.data.LauncherSettings
import com.slauncher.data.WallpaperMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    settings: LauncherSettings,
    onWallpaperModeChange: (WallpaperMode) -> Unit,
    onDarkenWallpaperChange: (Boolean) -> Unit,
    onExpressiveThemingChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp),
                shape = RoundedCornerShape(2.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Background Style Section Title
            Text(
                text = stringResource(R.string.bg_section_title),
                style = MaterialTheme.typography.labelLarge,
                fontFamily = FontFamily.Default,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mode Selector: Wallpaper vs Solid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wallpaper Option Card
                SelectionCard(
                    title = stringResource(R.string.bg_mode_wallpaper),
                    icon = Icons.Rounded.Wallpaper,
                    isSelected = settings.wallpaperMode == WallpaperMode.WALLPAPER,
                    onClick = { onWallpaperModeChange(WallpaperMode.WALLPAPER) },
                    modifier = Modifier.weight(1f)
                )

                // Solid Color Option Card
                SelectionCard(
                    title = stringResource(R.string.bg_mode_solid),
                    icon = Icons.Rounded.ColorLens,
                    isSelected = settings.wallpaperMode == WallpaperMode.SOLID,
                    onClick = { onWallpaperModeChange(WallpaperMode.SOLID) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-options depending on selected mode
            if (settings.wallpaperMode == WallpaperMode.WALLPAPER) {
                val isDark = isSystemInDarkTheme()
                // Option to darken or lighten the wallpaper
                ToggleRow(
                    title = stringResource(if (isDark) R.string.darken_wallpaper else R.string.lighten_wallpaper),
                    description = stringResource(if (isDark) R.string.darken_wallpaper_desc else R.string.lighten_wallpaper_desc),
                    checked = settings.darkenWallpaper,
                    onCheckedChange = onDarkenWallpaperChange
                )
            }

            // Material Expressive Theming toggle (applies to solid mode and UI containers)
            ToggleRow(
                title = stringResource(R.string.expressive_theming),
                description = stringResource(R.string.expressive_theming_desc),
                checked = settings.useMaterialExpressiveTheming,
                onCheckedChange = onExpressiveThemingChange
            )
        }
    }
}

@Composable
private fun SelectionCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer

    Surface(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontFamily = FontFamily.Default,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Default,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
