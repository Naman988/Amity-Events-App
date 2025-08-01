plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.example.collegeeventapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.collegeeventapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    // Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.firebase.crashlytics.buildtools)
    // Navigation Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")
    //Firestore Dependency
    implementation("com.google.firebase:firebase-firestore-ktx")
    //serilization Dependency
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
     // to store enrollment number
    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation("com.google.code.gson:gson:2.10.1")






    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
