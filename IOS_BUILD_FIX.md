# iOS Build Fix Instructions

## Issue
The iOS app cannot find the `ComposeApp` module dependency.

## Root Cause
After the project refactoring, the iOS framework needs to be rebuilt and the Xcode project may need to be updated.

## Solution Steps

### Step 1: Clean Build
```bash
cd /Users/hossamatef/Desktop/KMP_POC

# Clean everything
./gradlew clean
rm -rf composeApp/build/
rm -rf .gradle/
```

### Step 2: Rebuild iOS Framework
```bash
# For iOS Simulator (ARM64)
./gradlew :composeApp:compileKotlinIosSimulatorArm64

# For iOS Device (ARM64)
./gradlew :composeApp:compileKotlinIosArm64

# For iOS Simulator (x64 - Intel Macs)
./gradlew :composeApp:compileKotlinIosX64
```

### Step 3: Xcode Configuration

1. **Open Xcode:**
   ```bash
   open iosApp/iosApp.xcodeproj
   ```

2. **Check Framework Path:**
   - Select `iosApp` project in navigator
   - Go to `Build Settings` tab
   - Search for "Framework Search Paths"
   - Ensure it includes: `$(SRCROOT)/../composeApp/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)`

3. **Check Framework Import:**
   - Go to `Build Phases` tab
   - Expand "Link Binary With Libraries"
   - Ensure `ComposeApp.framework` is listed
   - If not, add it by clicking `+` and selecting the framework

4. **Verify Import Statement:**
   - Open `ContentView.swift`
   - The import should be: `import ComposeApp`
   - This should work after framework rebuild

### Step 4: Alternative Build Method

If the above doesn't work, try building from Xcode:

1. **Select iOS Scheme:**
   - In Xcode, select `iosApp` scheme
   - Choose iOS Simulator as target

2. **Product → Build (⌘+B)**
   - This will automatically trigger KMP framework build
   - May take longer on first build

3. **Product → Run (⌘+R)**
   - If build succeeds, run the app

### Step 5: Troubleshooting

**If still getting "ComposeApp not found":**

1. **Check Framework Generation:**
   ```bash
   ls -la composeApp/build/xcode-frameworks/Debug/iphonesimulator*/
   ```
   - You should see `ComposeApp.framework`

2. **Clean Xcode Derived Data:**
   - Xcode → Preferences → Locations → Derived Data
   - Click arrow to open in Finder
   - Delete the folder for this project
   - Try building again

3. **Reset iOS Simulator:**
   - iOS Simulator → Device → Erase All Content and Settings
   - Try running again

**If framework name is different:**

Check the actual framework name:
```bash
find composeApp/build -name "*.framework" -type d
```

If it's not `ComposeApp.framework`, update the import in `ContentView.swift` accordingly.

### Step 6: Verify Success

After successful build:
1. iOS Simulator should launch
2. App should show your Kotlin Multiplatform UI
3. No import errors in Xcode

## Prevention

To avoid this issue in future:
1. Always run `./gradlew clean` after major refactoring
2. Rebuild iOS frameworks when changing module structure
3. Keep Xcode and Android Studio versions up to date

## Framework Configuration

The framework is configured in `composeApp/build.gradle.kts`:
```kotlin
iosTarget.binaries.framework {
    baseName = "ComposeApp"  // This is your import name
    isStatic = true
}
```

If you change `baseName`, update the Swift import statement accordingly.