pluginManagement {
    repositories {
        google()
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

rootProject.name = "TheMovieDb"
include(":app")
include(":core:data")
include(":core:database")
include(":core:domain")
include(":core:model")
include(":core:network")
include(":core:testing")
include(":core:ui")
include(":feature:genres")
include(":feature:movies")
