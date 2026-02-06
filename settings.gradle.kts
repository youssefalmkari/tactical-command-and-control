pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Tactical Command And Control"

include(":app")

// Core modules
include(":core:domain")
include(":core:data")
include(":core:database")
include(":core:network")
include(":core:common")
include(":core:ui")

// Feature modules
include(":feature:mission-planning")
include(":feature:live-ops")
include(":feature:drone-control")
