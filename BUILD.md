# Building the myPeC APK

myPeC is a native Android app (Kotlin + Jetpack Compose). You build the installable APK with Android Studio. No command-line setup is required if you use Android Studio (it bundles the JDK and Android SDK).

## 1. Install Android Studio
Download and install the latest Android Studio from https://developer.android.com/studio
(During setup, let it install the Android SDK and an emulator if you want to test on a virtual device.)

## 2. Open the project
1. Launch Android Studio.
2. Choose "Open" and select this folder: `D:\Workout`.
3. Wait for "Gradle sync" to finish. The first sync downloads Gradle 8.11.1 and all dependencies, so it can take several minutes.
   - If prompted to install a missing SDK component (e.g. SDK Platform 35 / Build-Tools), click to accept and install.
   - Android Studio automatically creates `local.properties` pointing at your SDK. If it doesn't, add a file `local.properties` in the project root with:
     `sdk.dir=C:\\Users\\<you>\\AppData\\Local\\Android\\Sdk`

## 3. Build the APK
- Menu: Build > Build App Bundle(s) / APK(s) > Build APK(s).
- When it finishes, click "locate" in the popup, or find it at:
  `app/build/outputs/apk/debug/app-debug.apk`

Alternatively, from the built-in Terminal in Android Studio:
```
gradlew assembleDebug
```
(The output APK path is the same.)

## 4. Install on your phone
1. Copy `app-debug.apk` to your phone (USB, Google Drive, etc.).
2. On the phone, tap the file and allow "Install from unknown sources" when prompted.
3. Open myPeC. On first launch your 4-day split (plus the optional Saturday options) is loaded automatically.

## Notes
- Everything is stored locally on the device (Room database). Use Settings > Export data to back up as JSON.
- All weights are in kilograms.
- For workout reminders to show on Android 13+, allow the notifications permission when the system asks (or enable it for myPeC in system settings).
- To build a shareable signed release APK later: Build > Generate Signed Bundle / APK, create a keystore, and choose "release".

## Requirements summary
- Android Studio (latest), which provides JDK 17 + Android SDK
- compileSdk / targetSdk 35, minSdk 26 (Android 8.0+)
