# android-library Migration - Quick Start Guide

## Summary

You now have **android-library** cloned to `Project02`. This library needs AndroidX migration before Project01 can upgrade to Android 15/16.

## Current Status

✅ **Project01** - Working baseline with AndroidX + SDK 33  
⏳ **Project02** - android-library (needs migration)

## Quick Migration Steps

### 1. Create Gradle Wrapper (if needed from Windows)

The library uses Gradle 5.4.1. You'll need gradlew.bat for Windows builds.

### 2. Run the Same Migration Process

The android-library needs the **exact same migration** we just did for Project01:

```bash
cd /mnt/c/dev/AndroidStudioProjects/Project02

# Copy the migration script from Project01
cp ../Project01/migrate_androidx.py .

# Run it
python3 migrate_androidx.py
```

### 3. Update build.gradle

Same changes as Project01:
- Gradle: 5.4.1 → 7.6.4
- AGP: 3.5.4 → 7.4.2
- compileSdk: 31 → 33
- minSdk: 9 → 24
- targetSdk: 23 → 33
- Enable AndroidX

### 4. Update Dependencies

Replace in `build.gradle`:
```gradle
// OLD
implementation 'com.android.support:...'

// NEW  
implementation 'androidx...'
implementation 'com.google.android.material:material:1.9.0'
```

### 5. Build and Publish Locally

```bash
# From Windows (faster)
cd C:\dev\AndroidStudioProjects\Project02
set JAVA_HOME=C:\Program Files\Java\jdk-11.0.30+7
gradlew.bat clean build publishToMavenLocal
```

This publishes to: `C:\Users\siebaldu\.m2\repository\com\github\axet\android-library\`

### 6. Update Project01 to Use Local Library

In Project01's `app/build.gradle`:
```gradle
dependencies {
    // Use local migrated version
    implementation 'com.github.axet:android-library:1.39.0-androidx'
    // ... rest of dependencies
}
```

Add to Project01's `build.gradle`:
```gradle
allprojects {
    repositories {
        mavenLocal()  // Add this first
        google()
        mavenCentral()
    }
}
```

### 7. Test Project01 with Migrated Library

Build Project01 again to verify it works with the migrated library.

### 8. Then Upgrade to Android 15/16

Once the library is migrated and Project01 builds successfully:
- Upgrade to AGP 8.5.2
- Upgrade to SDK 35
- Build for Android 15/16

## Estimated Time

- android-library migration: 1-2 hours
- Testing with Project01: 30 minutes
- Final upgrade to SDK 35: 1 hour

**Total**: 2-3 hours

## Files to Modify in Project02

1. `gradle/wrapper/gradle-wrapper.properties` - Gradle version
2. `build.gradle` - AGP, SDK versions, dependencies
3. All `src/**/*.java` files - AndroidX imports (automated)
4. All `src/**/res/**/*.xml` files - AndroidX references (automated)

## The Migration Script

The same `migrate_androidx.py` from Project01 will work. It handles:
- 197 support library imports
- XML layout references
- Dependency class names

## Next Session

When you're ready to continue, we can:
1. Run the migration script on Project02
2. Update the build configuration
3. Build and publish locally
4. Test with Project01
5. Complete the upgrade to Android 15/16

---

**You now have a clear path forward!** The hard work (figuring out the migration process) is done. Project02 just needs the same treatment.
