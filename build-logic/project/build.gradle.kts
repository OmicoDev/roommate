plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(com.diffplug.spotless)
    implementation(consensusGradlePlugins)
    implementation(embeddedKotlin("gradle-plugin"))
    implementation(gradmGeneratedJar)
}
