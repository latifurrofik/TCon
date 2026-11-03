plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val releaseStoreFile = providers.environmentVariable("TCON_RELEASE_STORE_FILE").orNull
val releaseStorePassword = providers.environmentVariable("TCON_RELEASE_STORE_PASSWORD").orNull
val releaseKeyAlias = providers.environmentVariable("TCON_RELEASE_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("TCON_RELEASE_KEY_PASSWORD").orNull
val hasReleaseSigning = listOf(releaseStoreFile, releaseStorePassword, releaseKeyAlias, releaseKeyPassword)
    .all { !it.isNullOrBlank() }
val releaseTaskRequested = gradle.startParameter.taskNames.any { it.contains("release", ignoreCase = true) }
if (releaseTaskRequested && !hasReleaseSigning) {
    throw GradleException("Release signing is not configured. Set TCON_RELEASE_STORE_FILE, TCON_RELEASE_STORE_PASSWORD, TCON_RELEASE_KEY_ALIAS, and TCON_RELEASE_KEY_PASSWORD.")
}

android {
    namespace = "com.tcon.companion"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.tcon.companion"
        minSdk = 26
        targetSdk = 35
        versionCode = providers.environmentVariable("TCON_VERSION_CODE").orNull?.toIntOrNull() ?: 1
        versionName = providers.environmentVariable("TCON_VERSION_NAME").orNull ?: "0.1.0"
    }
    if (hasReleaseSigning) {
        signingConfigs {
            create("release") {
                storeFile = file(releaseStoreFile!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }
    buildTypes {
        getByName("release") {
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("androidx.core:core-ktx:1.15.0")
    testImplementation("junit:junit:4.13.2")
}
