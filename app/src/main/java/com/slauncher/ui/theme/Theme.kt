package com.slauncher.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.slauncher.data.LauncherSettings

// Pure AMOLED Monochromatic Dark Scheme
private val PureDarkColorScheme = darkColorScheme(
    primary = PureAmoledWhite,
    onPrimary = PureAmoledBlack,
    surface = PureAmoledBlack,
    onSurface = PureAmoledWhite,
    surfaceContainer = PureDarkSurfaceContainer,
    surfaceContainerHigh = PureDarkSearchBar,
    onSurfaceVariant = Color(0xFFC4C6D0),
    background = PureAmoledBlack,
    onBackground = PureAmoledWhite
)

// Pure Monochromatic Light Scheme
private val PureLightColorScheme = lightColorScheme(
    primary = PureAmoledBlack,
    onPrimary = PureAmoledWhite,
    surface = PureAmoledWhite,
    onSurface = PureAmoledBlack,
    surfaceContainer = PureLightSurfaceContainer,
    surfaceContainerHigh = PureLightSearchBar,
    onSurfaceVariant = Color(0xFF44474E),
    background = PureAmoledWhite,
    onBackground = PureAmoledBlack
)

// Static Fallback Expressive Schemes (when dynamic color is not supported by device)
private val FallbackExpressiveDarkColorScheme = darkColorScheme(
    primary = ExpressiveDarkPrimary,
    onPrimary = ExpressiveDarkOnPrimary,
    surface = ExpressiveDarkSurface,
    onSurface = ExpressiveDarkOnSurface,
    surfaceContainer = ExpressiveDarkSurfaceContainer,
    surfaceContainerHigh = ExpressiveDarkSurfaceContainerHigh,
    background = ExpressiveDarkBackground,
    onBackground = ExpressiveDarkOnSurface
)

private val FallbackExpressiveLightColorScheme = lightColorScheme(
    primary = ExpressiveLightPrimary,
    onPrimary = ExpressiveLightOnPrimary,
    surface = ExpressiveLightSurface,
    onSurface = ExpressiveLightOnSurface,
    surfaceContainer = ExpressiveLightSurfaceContainer,
    surfaceContainerHigh = ExpressiveLightSurfaceContainerHigh,
    background = ExpressiveLightBackground,
    onBackground = ExpressiveLightOnSurface
)

@Composable
fun SlauncherTheme(
    settings: LauncherSettings,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme: ColorScheme = if (settings.useMaterialExpressiveTheming) {
        // Material Expressive Theming: uses Android 12+ dynamic wallpaper-based palette
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if (darkTheme) FallbackExpressiveDarkColorScheme else FallbackExpressiveLightColorScheme
        }
    } else {
        // Pure Black & White (AMOLED / High contrast mode)
        if (darkTheme) PureDarkColorScheme else PureLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SystemTypography,
        content = content
    )
}
