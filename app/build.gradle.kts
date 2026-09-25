plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.minax333.wikihub"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.minax333.wikihub"
        minSdk = 23
        targetSdk = 37
        versionCode = 2
        versionName = "0.2.0"
    }

    signingConfigs {
        create("release") {
            storeFile = file(
                System.getenv("WIKIHUB_KEYSTORE_FILE")
                    ?: error("WIKIHUB_KEYSTORE_FILE is not set")
            )
            storePassword = System.getenv("WIKIHUB_KEYSTORE_PASSWORD")
                ?: error("WIKIHUB_KEYSTORE_PASSWORD is not set")
            keyAlias = System.getenv("WIKIHUB_KEY_ALIAS")
                ?: error("WIKIHUB_KEY_ALIAS is not set")
            keyPassword = System.getenv("WIKIHUB_KEY_PASSWORD")
                ?: error("WIKIHUB_KEY_PASSWORD is not set")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.09.00")

    implementation(composeBom)

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
