package com.atlan.performance.shared.platform

/**
 * JVM `actual` for the desktop verification target. This target ships no production UI; it exists so
 * the shared core and `commonTest` suite can be compiled and run with only a JDK
 * (`./gradlew :shared:jvmTest`) — no Android SDK, no Xcode, no IDE.
 */
actual class Platform actual constructor() {
    actual val name: String =
        "JVM ${System.getProperty("java.version")}"
}
