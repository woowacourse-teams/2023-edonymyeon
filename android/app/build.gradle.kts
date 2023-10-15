import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

    kotlin("plugin.serialization") version "1.8.21"

    id("com.google.dagger.hilt.android")
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "app.edonymyeon"
    compileSdk = 33

    defaultConfig {
        applicationId = "app.edonymyeon"
        minSdk = 28
        targetSdk = 33
        versionCode = 12
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "KAKAO_NATIVE_KEY",
            localProperties.getProperty("KAKAO_NATIVE_KEY"),
        )

        buildConfigField(
            "String",
            "KAKAO_APP_KEY",
            localProperties.getProperty("KAKAO_APP_KEY"),
        )

        buildConfigField(
            "String",
            "APP_BASE_URL",
            localProperties.getProperty("APP_BASE_URL"),
        )

        val kakaoNativeKey = localProperties.getProperty("KAKAO_NATIVE_KEY")
        manifestPlaceholders["KAKAO_NATIVE_KEY"] = kakaoNativeKey.substring(1, kakaoNativeKey.length - 1)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dataBinding {
        enable = true
    }

    packagingOptions {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // mock
    testImplementation("io.mockk:mockk-android:1.13.5")

    // constraint layout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // recycler view
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // okhttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // kotlinx-serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // domain module
    implementation(project(":domain"))

    // android core
    implementation("androidx.fragment:fragment-ktx:1.5.7")

    val lifecycle_version = "2.2.0"
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // by viewModels() 종속성
    implementation("androidx.activity:activity-ktx:1.7.2")

    // EncryptedSharedPreferences
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")

    // android chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // firebase 연동
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))

    // firebase crashlytics & analytics
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // kakao login SDK
    implementation("com.kakao.sdk:v2-user:2.10.0")

    // swipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // firebase cloud messaging
    implementation("com.google.firebase:firebase-messaging-ktx:23.2.1")

    // swipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
}
