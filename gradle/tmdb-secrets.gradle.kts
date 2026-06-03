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

extra["tmdbAccessTokenBuildConfigValue"] = tmdbAccessToken.toBuildConfigString()
