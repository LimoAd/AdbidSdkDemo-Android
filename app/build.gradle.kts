plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.yiman.ad.adbid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.adbid.sdk"
        //applicationId = "com.moguo.farmGameUnique"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("mySign") { // 或者使用 create("debug") 如果不存在名为 "debug" 的配置
            storeFile = file("./key/ydyFarm.keystore")
            storePassword = "123456"
            keyAlias = "aprilIdiom"
            keyPassword = "123456"
            enableV2Signing = true // 在 Kotlin DSL 中，v2SigningEnabled 变为 enableV2Signing
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs["mySign"]
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }

        getByName("debug") {
            signingConfig = signingConfigs["mySign"]
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.glide)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)


    implementation("com.limo.sdk:adbid-sdk:2.0.1")
}