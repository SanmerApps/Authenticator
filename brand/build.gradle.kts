plugins {
    alias(libs.plugins.self.library)
}

android {
    namespace = "dev.sanmer.brand"
}

dependencies {
    implementation(libs.androidx.annotation)
}
