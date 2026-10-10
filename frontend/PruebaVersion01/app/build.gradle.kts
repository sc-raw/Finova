plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.Finova"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.Finova"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    // 👇 Esto faltaba para alinear el compilador con la versión de Kotlin actual
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // --- Compose Navigation ---
    implementation(libs.androidx.navigation.compose)

    // --- Material Icons Extended ---
    implementation(libs.androidx.material.icons.extended)

    // --- Jetpack Compose Integration ---
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- ViewModel para Compose ---
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- Retrofit & Gson ---
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // --- ML Kit Text Recognition ---
    implementation(libs.play.services.mlkit.text.recognition)
}