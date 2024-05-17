plugins {
    `kotlin-dsl`
    id("roommate.publishing")
}

kotlin {
    jvmToolchain(8)
}

gradlePlugin {
    plugins {
        register("roommate") {
            id = "dev.omico.roommate"
            implementationClass = "dev.omico.roommate.RoommatePlugin"
        }
    }
}

dependencies {
    compileOnly(embeddedKotlin("gradle-plugin"))
}
