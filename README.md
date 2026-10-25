# Minimalist Lightweight Android Launcher

A sleek, lightweight, minimalist Android launcher written in **Kotlin** implementing **Material 3 Expressive** guidelines and one-handed reachability principles.

## Project Structure

```
MinimalistLauncher/
├── app/
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/minimalist/launcher/
│   │   │   ├── MainActivity.kt               # Edge-to-edge, Package monitor receiver
│   │   │   ├── data/
│   │   │   │   ├── AppModel.kt               # App data model & launch/uninstall actions
│   │   │   │   ├── LauncherSettings.kt       # WallpaperMode & settings state
│   │   │   │   └── SettingsRepository.kt     # SharedPreferences & StateFlow
│   │   │   └── ui/
│   │   │       ├── LauncherViewModel.kt      # Querying apps, sorting, search filter
│   │   │       ├── LauncherScreen.kt         # One UI reachability, bottom bar, wallpaper
│   │   │       ├── components/
│   │   │       │   ├── AppListItem.kt        # Circular icon & app title item
│   │   │       │   ├── SearchBarView.kt      # Rounded search pill & 3-dots button
│   │   │       │   └── SettingsBottomSheet.kt# Material 3 Expressive settings sheet
│   │   │       └── theme/
│   │   │           ├── Color.kt              # AMOLED, Wallpaper Scrim & M3 colors
│   │   │           ├── Type.kt               # System font typography
│   │   │           └── Theme.kt              # Dynamic colors & AMOLED switcher
│   │   └── res/
│   │       ├── values/themes.xml             # windowShowWallpaper & transparent window
│   │       └── values-night/themes.xml
```

---

## How to Build and Run

### 1. In Android Studio
1. Open Android Studio.
2. Select **Open** and choose the `MinimalistLauncher` directory.
3. Allow Gradle to sync.
4. Run on an Android device or emulator running **Android 8.0+ (API 26+)**, optimal on **Android 12+ (API 31+)** for dynamic color theming.

### 2. Via Command Line
```bash
cd MinimalistLauncher
./gradlew assembleDebug
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

To install directly to a connected device:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
