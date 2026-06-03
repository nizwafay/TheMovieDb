plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.papay.themoviedb.core.database"

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
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    ksp(libs.androidx.room.compiler)
}
