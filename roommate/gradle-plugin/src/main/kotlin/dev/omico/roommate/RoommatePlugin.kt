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

import dev.omico.roommate.internal.RoommateExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

@Suppress("unused")
class RoommatePlugin : Plugin<Project> {
    private val kgpPluginIds = listOf(
        "org.jetbrains.kotlin.android",
        "org.jetbrains.kotlin.jvm",
        "org.jetbrains.kotlin.multiplatform",
    )

    override fun apply(target: Project): Unit = kgpPluginIds.forEach { pluginId -> target.applyRoommate(pluginId) }

    private fun Project.applyRoommate(pluginId: String): Unit =
        plugins.withId(pluginId) {
            extensions.create(
                publicType = RoommateExtension::class,
                name = "roommate",
                instanceType = RoommateExtensionImpl::class,
                constructionArguments = arrayOf(
                    resources,
                    dependencies,
                    extensions.getByName("kotlin") as KotlinProjectExtension,
                ),
            )
        }
}
