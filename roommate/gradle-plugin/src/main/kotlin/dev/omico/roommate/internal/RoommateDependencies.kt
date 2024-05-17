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

import org.gradle.api.resources.ResourceHandler
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory

internal class RoommateDependencies(
    resources: ResourceHandler,
    roomVersion: String,
) {
    private val sqliteVersion: String =
        requireNotNull(resources.extractSqliteVersion(roomVersion)) {
            "Failed to extract sqlite version from room version $roomVersion"
        }

    val roomCompiler: String = "androidx.room:room-compiler:$roomVersion"
    val roomRuntime: String = "androidx.room:room-runtime:$roomVersion"
    val sqliteBundle: String = "androidx.sqlite:sqlite-bundled:$sqliteVersion"
}

@Suppress("NestedBlockDepth")
private fun ResourceHandler.extractSqliteVersion(roomVersion: String): String? {
    val roomRuntimePomUrl =
        "https://dl.google.com/android/maven2/androidx/room/room-runtime/$roomVersion/room-runtime-$roomVersion.pom"
    val pomContent = text.fromUri(roomRuntimePomUrl).asString()
    val document = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse(pomContent.byteInputStream())
    var group: String? = null
    var artifact: String? = null
    var version: String? = null
    var sqliteVersion: String? = null
    document.getElementsByTagName("dependencies").forEach { node ->
        node.childNodes.forEach { childNode ->
            childNode.childNodes.forEach {
                when (it.nodeName) {
                    "groupId" -> group = it.textContent
                    "artifactId" -> artifact = it.textContent
                    "version" -> version = it.textContent
                }
            }
            if ("androidx.sqlite" == group && "sqlite-framework" == artifact) {
                sqliteVersion = version
            }
        }
    }
    return sqliteVersion
}

private fun NodeList.forEach(transform: (Node) -> Unit): Unit = repeat(length) { index -> transform(item(index)) }
