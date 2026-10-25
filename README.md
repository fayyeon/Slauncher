# Minimalist Lightweight Android Launcher

A sleek, lightweight, minimalist Android launcher written in **Kotlin** implementing **Material 3 Expressive** guidelines and one-handed reachability principles.

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
