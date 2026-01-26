import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.serialization)
}

android {
    namespace = "ninja.bryansills.photogallery"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "ninja.bryansills.photogallery"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("17")
        optIn = listOf("kotlin.time.ExperimentalTime")
    }
}

dependencies {
    implementation(libs.flickrj.android)
    implementation(files("libs/slf4j-android-1.6.1-rc1.jar"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.navigation)
    implementation(libs.serialization.json)
    implementation(libs.hilt.compose)
    coreLibraryDesugaring(libs.desugar)
    implementation(libs.datetime)
    implementation(libs.coil.compose)
    implementation(libs.coil.okhttp)
    implementation(libs.paging)
    implementation(libs.telephoto)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.icons)
    testImplementation(libs.junit)

    testImplementation(libs.turbine)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.coroutine.test)
    testImplementation(libs.navigation.test)
    testImplementation(libs.paging.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(libs.coroutine.test)
    androidTestImplementation(libs.navigation.test)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}