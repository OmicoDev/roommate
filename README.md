# Roommate

Roommate is a companion plugin that simplifies the introduction of Jetpack Room configuration. It can automatically find the Jetpack Sqlite version that matches Jetpack Room.

## Usage

### Add the Roommate dependency

```kotlin
// <root>/settings.gradle.kts
pluginManagement {
    repositories {
        maven(url = "https://maven.omico.me")
        gradlePluginPortal()
    }
}

// <root>/build.gradle.kts
plugins {
    // Apply either of the Kotlin plugins first.
    id("org.jetbrains.kotlin.android") version "<version>" apply false
    id("org.jetbrains.kotlin.jvm") version "<version>" apply false
    id("org.jetbrains.kotlin.multiplatform") version "<version>" apply false
    // Then apply the Roommate plugin.
    id("dev.omico.roommate") version "<version>" apply false
}
```

### Configure the Roommate plugin

```kotlin
// <root>/database/build.gradle.kts
plugins {
    // Apply the Roommate plugin.
    id("dev.omico.roommate")
}

// The following configuration will be configured by Roommate.
//kotlin {
//    sourceSets {
//        commonMain {
//            dependencies {
//                implementation(androidx.room.runtime)
//            }
//        }
//
//        desktopMain {
//            dependencies {
//                implementation(androidx.sqlite.bundled)
//            }
//        }
//    }
//}
//
//dependencies {
//    kspAndroid(androidx.room.compiler)
//    kspDesktop(androidx.room.compiler)
//}

roommate {
    // The version of the Room library.
    roomVersion(versions.androidx.room)

    // Add the Room compiler.
    // If you only have Android & Desktop targets, withKspRoomCompiler() is same as the following.
    // withKspRoomCompiler { target ->
    //    target.platformType == KotlinPlatformType.jvm || target.platformType == KotlinPlatformType.androidJvm
    // }
    withKspRoomCompiler()

    dependencies {
        commonMainImplementation(roomPaging)
        desktopMainImplementation(sqliteBundle)
    }
}
```

## License

```
Copyright 2024 Omico

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
