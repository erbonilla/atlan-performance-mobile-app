// Root build file. Plugins are declared here with `apply false` and applied in modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    // Declared here (apply false) so the whole build resolves one plugin classpath and the Kotlin
    // Gradle plugin stays at the catalog version — SQLDelight's transitive KGP must not downgrade it.
    alias(libs.plugins.sqldelight) apply false
}
