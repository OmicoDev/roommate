import me.omico.consensus.api.dsl.isAutomatedPublishingGradlePlugin

plugins {
    id("me.omico.consensus.publishing")
}

consensus {
    publishing {
        when {
            isCi -> publishToNexusRepository()
            else -> publishToLocalRepository("MAVEN_OMICO_LOCAL_URI")
        }
        signing {
            if (isSnapshot) return@signing
            useGpgCmd()
            sign(publications)
        }
        afterEvaluate {
            if (!isAutomatedPublishingGradlePlugin) {
                createMavenPublication {
                    from(components["java"])
                }
            }
            publications.all {
                if (this !is MavenPublication) return@all
                pom {
                    name = providers.gradleProperty("POM_NAME")
                    description = providers.gradleProperty("POM_DESCRIPTION")
                    url = "https://github.com/OmicoDev/roommate"
                    licenses {
                        license {
                            name = "The Apache Software License, Version 2.0"
                            url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                        }
                    }
                    developers {
                        developer {
                            id = "Omico"
                            name = "Omico"
                        }
                    }
                    scm {
                        url = "https://github.com/OmicoDev/roommate"
                        connection = "scm:git:https://github.com/OmicoDev/roommate.git"
                        developerConnection = "scm:git:https://github.com/OmicoDev/roommate.git"
                    }
                }
            }
        }
    }
}

extensions.findByType<JavaPluginExtension>()?.apply {
    withSourcesJar()
    withJavadocJar()
}
