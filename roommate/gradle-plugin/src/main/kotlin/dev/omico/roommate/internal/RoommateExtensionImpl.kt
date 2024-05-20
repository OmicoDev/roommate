/*
 * Copyright 2024 Omico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.omico.roommate.internal

import dev.omico.roommate.RoommateExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.resources.ResourceHandler
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import javax.inject.Inject

internal abstract class RoommateExtensionImpl @Inject constructor(
    private val plugins: PluginContainer,
    private val resources: ResourceHandler,
    private val dependencies: DependencyHandler,
    private val kotlinProjectExtension: KotlinProjectExtension,
) : RoommateExtension {
    private lateinit var roommateDependencies: RoommateDependencies

    override fun roomVersion(version: String) {
        roommateDependencies = RoommateDependencies(resources, version)
        when (kotlinProjectExtension) {
            is KotlinSingleTargetExtension<*> ->
                dependencies.add("implementation", roommateDependencies.roomRuntime)
            is KotlinMultiplatformExtension ->
                dependencies.add("commonMainImplementation", roommateDependencies.roomRuntime)
        }
    }

    override fun withKsp(vararg targets: String) {
        checkRoomVersionIsPresent()
        check(plugins.hasPlugin("com.google.devtools.ksp")) {
            "The com.google.devtools.ksp plugin must be applied in the plugins block before calling withKsp."
        }
        plugins.withId("com.google.devtools.ksp") {
            when (kotlinProjectExtension) {
                is KotlinSingleTargetExtension<*> ->
                    dependencies.add("ksp", roommateDependencies.roomCompiler)
                is KotlinMultiplatformExtension ->
                    kotlinProjectExtension.targets.configureEach {
                        if (name !in targets) return@configureEach
                        dependencies.add("ksp${name.capitalized()}", roommateDependencies.roomCompiler)
                    }
            }
        }
    }

    override fun applySqliteBundleTo(vararg configurations: String) {
        checkRoomVersionIsPresent()
        configurations.forEach { configuration ->
            dependencies.add(configuration, roommateDependencies.sqliteBundle)
        }
    }

    private fun checkRoomVersionIsPresent(): Unit =
        check(::roommateDependencies.isInitialized) { "The roomVersion must be set before calling other functions." }
}
