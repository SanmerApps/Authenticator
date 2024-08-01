plugins {
    alias(libs.plugins.self.library)
}

android {
    namespace = "dev.sanmer.core"
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.encoding.base32)
    implementation(libs.encoding.base64)
    implementation(libs.google.zxing)
}
