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
    ksp(libs.roomCompiler)

    // WorkManager
    implementation(libs.workRuntimeKtx)

    // Coil
    implementation(libs.coilCompose)


    //messaging
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)



}
apply(plugin = "com.google.gms.google-services")
