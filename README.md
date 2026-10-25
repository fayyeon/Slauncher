# Slauncher - Minimalist Lightweight Android Launcher

A sleek, lightweight, minimalist Android launcher written in **Kotlin** and **Jetpack Compose**, implementing **Material 3 Expressive** guidelines and one-handed reachability principles.

---

## Features & Criteria Fulfillment

| # | Criterion | Implementation Details |
|---|-----------|------------------------|
| **1** | **Scrollable list instead of icon grid** | Clean vertical list built with Compose `LazyColumn`. Displays circular app icons paired with high-contrast app titles. Tap to launch instantly, long-press for App Info and Uninstall shortcuts. |
| **2** | **3-Dots Hamburger Menu with Wallpaper & Color Controls** | Positioned at bottom right. Opens a Material 3 Expressive bottom sheet with: <br>• **Wallpaper Mode**: Transparent activity window with an optional **Darken Wallpaper** scrim toggle.<br>• **Solid Color Mode**: Automatic Dark/Light mode, with **Material Expressive Theming** toggle (dynamic wallpaper-based colors) vs Pure B&W (AMOLED).<br>• Theme-adaptive buttons. |
| **3** | **No Launchable Icon in App Drawers** | In `AndroidManifest.xml`, registered strictly with `category.HOME` and `category.DEFAULT` — **omitting** `category.LAUNCHER`. It only activates when chosen in Android *Settings → Default Apps → Home app*. |
| **4** | **Pill Search Bar** | Rounded search bar (`28.dp` corner radius) placed alongside the 3-dots button at the bottom. Provides real-time filtering, clear ("X") button, and keyboard Search action to launch the top match directly. |
| **5** | **Zero Animation Code** | No `animate*AsState`, no `AnimatedVisibility`, no transitions or spring physics. Instantaneous rendering and state transitions for ultra-fast performance. |
| **6** | **Alphabetical Sorting** | Apps queried from `PackageManager` are strictly sorted case-insensitively using `compareBy(String.CASE_INSENSITIVE_ORDER) { it.label }`. Filtered search queries maintain this ordering. |
| **7** | **System Font Exclusively** | Typography explicitly enforces `FontFamily.Default` without any bundled or downloaded custom fonts, honoring the user's system font preference. |
| **8** | **One-Handed Reachability** | The top of the list begins ~42% down the screen, placing the first app right in thumb reach. When the user scrolls up, the list glides up to fill the screen seamlessly. |

---

## Project Structure

```
Slauncher/
├── app/
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/slauncher/
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

## 🚀 How to Build and Run

### 1. In Android Studio
1. Open Android Studio.
2. Select **Open** and choose the `Slauncher` directory.
3. Allow Gradle to sync.
4. Run on an Android device or emulator running **Android 8.0+ (API 26+)**, optimal on **Android 12+ (API 31+)** for dynamic color theming.

### 2. Via Command Line
```bash
cd Slauncher
./gradlew assembleDebug
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

To install directly to a connected device:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## ⚙️ Setting as Default Launcher

Since the app purposefully does **not** include a launcher drawer icon (Criterion 3):
1. Open your device's **Settings**.
2. Navigate to **Apps** → **Default apps** → **Home app**.
3. Select **Slauncher**.
4. Alternatively, press the Home button on your device — Android will display a prompt asking which app to use as Home. Select **Slauncher** and tap **Always**.

