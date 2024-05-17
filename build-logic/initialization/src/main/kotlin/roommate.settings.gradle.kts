import me.omico.gradle.initialization.includeAllSubprojectModules
import me.omico.gradm.addDeclaredRepositories

addDeclaredRepositories()

plugins {
    id("roommate.develocity")
    id("roommate.gradm")
}

includeBuild("build-logic/project")

includeAllSubprojectModules("roommate")
