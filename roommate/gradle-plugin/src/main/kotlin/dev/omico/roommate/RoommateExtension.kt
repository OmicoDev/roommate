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
package dev.omico.roommate

import dev.omico.roommate.internal.RoommateDependencies
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.provider.Property
import org.gradle.api.resources.ResourceHandler
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.property
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import javax.inject.Inject

open class RoommateExtension @Inject constructor(
    objects: ObjectFactory,
    private val plugins: PluginContainer,
    private val resources: ResourceHandler,
    private val dependencies: DependencyHandler,
    private val kotlinProjectExtension: KotlinProjectExtension,
) {
    private val roomVersion: Property<String> = objects.property()
    private lateinit var roommateDependencies: RoommateDependencies

    fun roomVersion(version: String) {
        roomVersion.set(version)
        roommateDependencies = RoommateDependencies(resources, version)
        when (kotlinProjectExtension) {
            is KotlinSingleTargetExtension<*> ->
                dependencies.add("implementation", roommateDependencies.roomRuntime)
            is KotlinMultiplatformExtension ->
                dependencies.add("commonMainImplementation", roommateDependencies.roomRuntime)
        }
    }

    fun withKsp(vararg targets: String) {
        checkRoomVersionIsPresent()
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

    fun applySqliteBundleTo(vararg configurations: String) {
        checkRoomVersionIsPresent()
        configurations.forEach { configuration ->
            dependencies.add(configuration, roommateDependencies.sqliteBundle)
        }
    }

    private fun checkRoomVersionIsPresent(): Unit =
        check(roomVersion.isPresent) { "The roomVersion must be set before calling other functions." }
}
