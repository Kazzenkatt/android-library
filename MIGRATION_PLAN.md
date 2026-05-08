# android-library Migration Plan

## Current State
- **Repository**: https://gitlab.com/axet/android-library
- **Location**: `/mnt/c/dev/AndroidStudioProjects/Project02/`
- **Type**: Android Library
- **Files**: 113 Java files
- **Support Library Imports**: 197 occurrences
- **Gradle**: 3.5.4
- **compileSdk**: 31
- **minSdk**: 9
- **targetSdk**: 23

## Migration Strategy

### Phase 1: Establish Baseline
1. ✅ Clone repository
2. Test current build
3. Create git branch for migration

### Phase 2: Upgrade Build System
1. Upgrade Gradle: 3.5.4 → 7.6.4
2. Upgrade AGP: 3.5.4 → 7.4.2
3. Replace jcenter() with mavenCentral()
4. Update minSdk: 9 → 24

### Phase 3: AndroidX Migration
1. Run automated migration script (197 imports)
2. Update dependencies to AndroidX
3. Enable AndroidX and Jetifier
4. Test build

### Phase 4: Publish Locally
1. Build library: `gradlew publishToMavenLocal`
2. Update Project01 to use local version
3. Test Project01 with migrated library

### Phase 5: Upgrade to SDK 33
1. Update compileSdk: 31 → 33
2. Update targetSdk: 23 → 33
3. Final build and publish

## Next Steps

Run baseline build to ensure it works before migration.
