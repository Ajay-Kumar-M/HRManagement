plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
//    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization")

}

android {
    namespace = "com.example.hrmanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hrmanagement"
        minSdk = 28
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //navigation
    implementation("androidx.navigation:navigation-compose:2.7.0")

    //Kotlinx IO Exceptions
    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.8.0")

    //Dagger - hilt - Used in UserSignInStatusRepository.kt
    implementation("com.google.dagger:hilt-android:2.55")
    kapt("com.google.dagger:hilt-compiler:2.55")
    // Hilt for dependency injection
//  implementation(libs.androidx.hilt.lifecycle.viewmodel)
    // Hilt integration with Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Retrofit
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.3")

    //ktor
//    implementation("io.ktor:ktor-client-core:3.1.3")
//    implementation("io.ktor:ktor-client-cio:3.1.3")
//    implementation("io.ktor:ktor-client-content-negotiation:3.1.3")
//    implementation("io.ktor:ktor-client-okhttp:3.1.3")
//    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.3")

    //firebase
    implementation("com.firebaseui:firebase-ui-auth:9.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    //firestore
    implementation("com.google.firebase:firebase-firestore")

    //SignInWithGoogle
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    //Preferences-Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    //serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    implementation("io.coil-kt:coil-compose:2.7.0")

    //material3
    implementation(libs.androidx.material3)

    //kotlin reflection
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")

    //kotlin datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

    //to observeasstate
    implementation("androidx.compose.runtime:runtime-livedata:1.8.2")

    //Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

}
