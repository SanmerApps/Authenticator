plugins {
    alias(libs.plugins.self.library)
}

android {
    namespace = "dev.sanmer.logo"
}

dependencies {
    implementation(libs.androidx.annotation)
}
