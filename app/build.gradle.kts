import java.time.Instant

plugins {
    alias(libs.plugins.self.application)
    alias(libs.plugins.self.compose)
    alias(libs.plugins.self.room)
    alias(libs.plugins.kotlin.serialization)
}

val baseVersionName = "0.2.5"
val gitCommitTag = gitCommitTag()
val gitCommitSha = gitCommitSha()
val gitCommitNum = gitCommitNum()
val devSuffix = if (gitCommitTag.isEmpty()) ".dev" else ""

android {
    namespace = "dev.sanmer.authenticator"

    defaultConfig {
        applicationId = namespace
        versionName = "${baseVersionName}.${gitCommitSha}${devSuffix}"
        versionCode = gitCommitNum
        ndk.abiFilters += listOf("arm64-v8a", "x86_64")
    }

    androidResources {
        generateLocaleConfig = true
        localeFilters += listOf("en", "zh-rCN")
    }

    val releaseSigning = if (hasReleaseKeyStore()) {
        signingConfigs.create("release") {
            storeFile = releaseKeyStore
            storePassword = releaseKeyStorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
            enableV3Signing = true
            enableV4Signing = true
        }
    } else {
        signingConfigs.getByName("debug")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            applicationIdSuffix = ".debug"
        }

        all {
            signingConfig = releaseSigning
            buildConfigField("String", "GIT_SHA", "\"$gitCommitSha\"")
            buildConfigField("long", "BUILD_TIME", Instant.now().toEpochMilli().toString())
        }
    }

    packaging {
        jniLibs.excludes += setOf(
            "**/libdatastore_shared_counter.so"
        )
        resources.excludes += setOf(
            "META-INF/**",
            "kotlin/**",
            "**.bin",
            "**.properties"
        )
    }

    dependenciesInfo.includeInApk = false
}

dependencies {
    implementation(project(":core"))
    implementation(project(":logo"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.protobuf)
}
