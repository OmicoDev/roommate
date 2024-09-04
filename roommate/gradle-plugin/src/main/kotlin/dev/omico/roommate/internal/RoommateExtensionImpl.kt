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
import dev.omico.roommate.internal.utility.capitalize
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import javax.inject.Inject

internal abstract class RoommateExtensionImpl @Inject constructor(
    private val project: Project,
) : RoommateExtension {
    private lateinit var roommateDependencies: RoommateDependencies

    override fun roomVersion(version: String) {
        roommateDependencies = RoommateDependencies(project.resources, version)
        when (project.kotlinExtension) {
            is KotlinSingleTargetExtension<*> ->
                project.dependencies.add("implementation", roommateDependencies.roomRuntime)
            is KotlinMultiplatformExtension ->
                project.dependencies.add("commonMainImplementation", roommateDependencies.roomRuntime)
        }
    }

    override fun withKspRoomCompiler(predicate: (KotlinTarget) -> Boolean) {
        require(project.plugins.hasPlugin("com.google.devtools.ksp")) {
            "The KSP plugin must be applied before calling withKspRoomCompiler."
        }
        when (val kotlinProjectExtension = project.kotlinExtension) {
            is KotlinSingleTargetExtension<*> -> project.dependencies.add("ksp", roommateDependencies.roomCompiler)
            is KotlinMultiplatformExtension ->
                kotlinProjectExtension.targets.configureEach {
                    if (platformType == KotlinPlatformType.common) return@configureEach
                    if (predicate(this).not()) return@configureEach
                    project.dependencies.add("ksp${name.capitalize()}", roommateDependencies.roomCompiler)
                }
        }
    }

    override val DependencyHandler.roomCompiler: String
        get() = requireRoommateDependencies().roomCompiler

    override val DependencyHandler.roomRuntime: String
        get() = requireRoommateDependencies().roomRuntime

    override val DependencyHandler.roomPaging: String
        get() = requireRoommateDependencies().roomPaging

    override val DependencyHandler.sqliteBundle: String
        get() = requireRoommateDependencies().sqliteBundle

    private fun requireRoommateDependencies(): RoommateDependencies {
        check(::roommateDependencies.isInitialized) { "The roomVersion must be set before calling other functions." }
        return roommateDependencies
    }
}
