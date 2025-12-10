plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.basu.vaccineremainder"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.basu.vaccineremainder"
        minSdk = 24
        targetSdk = 36
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Navigation
    implementation(libs.navigationCompose)

    // ViewModel + Runtime
    implementation(libs.lifecycleViewmodelCompose)
    implementation(libs.lifecycleRuntimeCompose)

    // Coroutines
    implementation(libs.coroutinesAndroid)

    // ROOM (with KSP)
    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.databinding.adapters)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation)
    // THE BAD LINE WAS HERE. IT IS NOW GONE.
    ksp(libs.roomCompiler)

    // WorkManager
    implementation(libs.workRuntimeKtx)

    // Coil
    implementation(libs.coilCompose)

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-functions-ktx")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation("com.google.firebase:firebase-auth-ktx")
    // --- END: CORRECTED FIREBASE DEPENDENCIES ---

    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("org.mindrot:jbcrypt:0.4")
}



apply(plugin = "com.google.gms.google-services")
