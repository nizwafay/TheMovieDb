plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.papay.themoviedb.core.data"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
}
