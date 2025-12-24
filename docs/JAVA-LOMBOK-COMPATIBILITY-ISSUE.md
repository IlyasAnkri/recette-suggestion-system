# Java 25 / Lombok Compatibility Issue

## Problem Summary

**Status:** BLOCKING compilation of all Java modules  
**Date Identified:** 2025-12-21  
**Severity:** HIGH - Prevents testing and deployment

## Root Cause

The development environment is using **Java 25.0.1**, but the project is configured for **Java 21**. Lombok version 1.18.34 (latest) does not yet support Java 25, causing compilation failure:

```
[ERROR] Fatal error compiling: java.lang.ExceptionInInitializerError: 
com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

## Current Environment

- **Java Version:** 25.0.1 (Oracle Corporation)
- **Maven Version:** 3.9.11
- **Lombok Version:** 1.18.34
- **Project Target:** Java 21
- **OS:** Windows 11

## Impact

### Blocked Activities
- ✗ Compilation of shared module
- ✗ Running unit/integration tests
- ✗ Building dependent services (recipe-search-service, ingredient-matching-service)
- ✗ Verification of Stories 2.4 and 2.5 implementations

### Completed But Unverified Work
- ✓ Story 2.4: Micrometer timers and MongoDB timeouts implemented
- ✓ Story 2.5: Cache metrics configuration and integration tests written
- ✓ Lombok configuration improvements (annotation processor paths, latest version)

## Resolution Options

### Option 1: Install Java 21 (RECOMMENDED)

**Steps:**
1. Download Java 21 LTS from [Oracle](https://www.oracle.com/java/technologies/downloads/#java21) or [Adoptium](https://adoptium.net/temurin/releases/?version=21)
2. Install Java 21
3. Set `JAVA_HOME` environment variable to Java 21 installation path
4. Update `PATH` to prioritize Java 21
5. Verify: `java -version` should show 21.x.x
6. Run: `mvn clean install` in `services/shared`

**Pros:**
- Project-specified Java version
- Full Lombok support
- Long-term stability (LTS release)

**Cons:**
- Requires system-level changes
- May affect other projects using Java 25

### Option 2: Use Maven Toolchains

**Steps:**
1. Install Java 21 (as above)
2. Create `~/.m2/toolchains.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>21</version>
    </provides>
    <configuration>
      <jdkHome>C:\Program Files\Java\jdk-21</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```
3. Run: `mvn clean install`

**Pros:**
- Keeps Java 25 as system default
- Maven automatically uses correct Java version
- Per-project Java version control

**Cons:**
- Requires Java 21 installation
- Additional configuration file

### Option 3: Wait for Lombok Java 25 Support

**Status:** Lombok team is working on Java 25 support  
**Timeline:** Unknown (Java 25 released September 2024)  
**Tracking:** https://github.com/projectlombok/lombok/issues

**Pros:**
- No environment changes needed

**Cons:**
- Indefinite wait time
- Blocks all development progress

### Option 4: Remove Lombok (NOT RECOMMENDED)

**Impact:** Would require manually generating:
- Getters/setters for all entity classes
- Builders for all DTOs
- Constructors (NoArgs, AllArgs)
- Logging fields
- Estimated effort: 4-6 hours

## Attempted Fixes

1. ✓ Updated Lombok: 1.18.30 → 1.18.32 → 1.18.34
2. ✓ Added annotation processor paths to maven-compiler-plugin
3. ✓ Updated maven-compiler-plugin to 3.13.0
4. ✓ Used `<release>21</release>` instead of source/target
5. ✓ Added explicit Maven compiler properties
6. ✗ All attempts failed with same TypeTag error

## Files Modified During Investigation

1. `services/shared/pom.xml`
   - Updated Lombok version to 1.18.34
   - Added annotation processor configuration
   - Updated maven-compiler-plugin to 3.13.0
   - Added `<release>21</release>` configuration

## Next Steps

**Immediate Action Required:**
1. Install Java 21 LTS
2. Configure environment to use Java 21
3. Verify compilation: `mvn clean install` in `services/shared`
4. Run tests for Stories 2.4 and 2.5
5. Update story files with test results

**After Resolution:**
- Document Java version requirements in README
- Add Java version check to CI/CD pipeline
- Consider adding Maven Enforcer Plugin to prevent future issues

## References

- [Lombok Java Compatibility](https://projectlombok.org/changelog)
- [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/)
- [Maven Toolchains](https://maven.apache.org/guides/mini/guide-using-toolchains.html)
