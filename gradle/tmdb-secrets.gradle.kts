import java.util.Properties

fun String.toBuildConfigString(): String =
    "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use(::load)
    }
}

val tmdbAccessToken = localProperties.getProperty("tmdb.accessToken")
    ?: providers.environmentVariable("TMDB_ACCESS_TOKEN").orNull
    ?: ""

val isReleaseBuild = gradle.startParameter.taskNames.any { taskName ->
    taskName.contains("Release", ignoreCase = true)
}

if (isReleaseBuild) {
    val sanitizedToken = tmdbAccessToken.trim()
    val legacyApiKeyPattern = Regex("^[0-9a-f]{32}$")

    require(sanitizedToken.isNotBlank()) {
        "TMDB access token is required for release builds. Add tmdb.accessToken to local.properties or set TMDB_ACCESS_TOKEN."
    }

    require(!legacyApiKeyPattern.matches(sanitizedToken)) {
        "Use the TMDB API Read Access Token for Bearer auth, not the legacy API key."
    }
}

extra["tmdbAccessTokenBuildConfigValue"] = tmdbAccessToken.toBuildConfigString()
