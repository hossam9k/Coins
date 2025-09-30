# Gradle JDK Configuration Fix

## Issue
Android Studio shows "Invalid Gradle JDK configuration found" with undefined `java.home` in gradle/config.properties.

## Solution Steps

### Option 1: Use Android Studio Embedded JDK (Recommended)

1. **Open Android Studio Settings:**
   - Go to `Android Studio` → `Settings` (macOS) or `File` → `Settings` (Windows/Linux)
   - Or use shortcut: `⌘ + ,` (macOS) or `Ctrl + Alt + S` (Windows/Linux)

2. **Navigate to Gradle Settings:**
   - Go to `Build, Execution, Deployment` → `Build Tools` → `Gradle`

3. **Set Gradle JDK:**
   - In the "Gradle JDK" dropdown, select:
   - **"Use Embedded JDK"** or
   - **"/Applications/Android Studio.app/Contents/jbr/Contents/Home"**

4. **Apply Settings:**
   - Click `Apply` then `OK`

### Option 2: Use Specific JDK Version

If you prefer to use a specific JDK:

1. **In Gradle Settings:**
   - Select `jbr-17` from dropdown:
   - **"/Users/hossamatef/Library/Java/JavaVirtualMachines/jbr-17.0.14/Contents/Home"**

2. **Verify JDK Version:**
   - This should be JDK 17 which is compatible with the project

### Option 3: Command Line Fix

You can also fix this via command line:

```bash
cd /Users/hossamatef/Desktop/KMP_POC

# Check current Java version
java -version

# Set JAVA_HOME if needed
export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home

# Run gradle with specific Java home
./gradlew build -Dorg.gradle.java.home=$JAVA_HOME
```

### Option 4: Create gradle.properties (If needed)

If the issue persists, create or update `gradle.properties`:

```bash
# Create/edit gradle.properties in project root
echo "org.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home" > gradle.properties
```

## Verification Steps

1. **Sync Project:**
   - Click `Sync Project with Gradle Files` button in Android Studio
   - Or use `File` → `Sync Project with Gradle Files`

2. **Test Build:**
   ```bash
   ./gradlew clean build
   ```

3. **Check in Android Studio:**
   - Open `Build` → `Build Project` (⌘ + F9)
   - Should build without JDK errors

## Why This Happens

This issue commonly occurs when:
- Android Studio updates and changes JDK paths
- Multiple JDK versions are installed
- Project is imported on a different machine
- Gradle cache contains old JDK references

## Prevention

To prevent this issue in future:
1. Always use "Embedded JDK" in Android Studio settings
2. Don't hardcode JDK paths in gradle.properties
3. Keep Android Studio updated
4. Use project-specific JDK settings rather than global ones

## Project Compatibility

This project requires:
- **JDK 11 or higher** (configured in build.gradle.kts)
- **Android Studio Ladybug** or newer
- **Gradle 8.14.3** (as specified in gradle wrapper)

The embedded JDK in Android Studio is always compatible and recommended for KMP projects.