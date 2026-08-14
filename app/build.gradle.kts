import com.android.build.api.variant.BuildConfigField
import java.time.Instant

plugins {
    alias(libs.plugins.self.application)
    alias(libs.plugins.self.compose)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
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
            optimization {
                enable = true
            }
        }

        debug {
            applicationIdSuffix = ".debug"
        }

        all {
            signingConfig = releaseSigning
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

androidComponents.onVariants { variant ->
    variant.buildConfigFields?.apply {
        put("GIT_SHA", BuildConfigField("String", "\"$gitCommitSha\"", null))
        put("BUILD_TIME", BuildConfigField("long", Instant.now().toEpochMilli().toString(), null))
    }

    variant.outputs.forEach { output ->
        output.outputFileName =
            output.versionName.zip(output.versionCode) { versionName, versionCode ->
                "Authenticator-$versionName-$versionCode-${variant.buildType}.apk"
            }
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(project(":brand"))
    implementation(project(":core"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.navigation3)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.protobuf)
}
