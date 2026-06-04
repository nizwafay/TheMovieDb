import org.gradle.api.tasks.testing.Test
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.detekt) apply false
    jacoco
}

jacoco {
    toolVersion = "0.8.13"
}

val jacocoCoveredProjects = setOf(
    ":app",
    ":core:data",
    ":core:domain",
    ":core:model",
    ":core:network",
    ":core:ui",
    ":feature:genres",
    ":feature:movies"
)

val jacocoClassExcludes = listOf(
    "**/BuildConfig.*",
    "**/R.class",
    "**/R$*.class",
    "**/Manifest*.*",
    "**/*Test*.*",
    "**/*ComposableSingletons*.*",
    "**/*Kt$*.*",
    "**/di/**",
    "**/MainActivity*.*",
    "**/TheMovieDbApplication*.*",
    "**/AppNavHost*.*",
    "**/ui/theme/**",
    "**/*Route*.*",
    "**/*Screen*.*",
    "**/*Content*.*",
    "**/*Card*.*",
    "**/*Snackbar*.*",
    "**/*Player*.*",
    "**/PaginationEffect*.*",
    "**/RefreshableContent*.*",
    "**/LoadingContent*.*"
)

subprojects {
    apply(plugin = "jacoco")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    jacoco {
        toolVersion = "0.8.13"
    }

    extensions.configure<DetektExtension>("detekt") {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        source.setFrom(
            files(
                "src/main/java",
                "src/test/java"
            )
        )
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "17"
        exclude("**/build/**")
        reports {
            html.required.set(true)
            xml.required.set(true)
            md.required.set(false)
            sarif.required.set(false)
        }
    }

    tasks.withType<Test>().configureEach {
        extensions.configure<JacocoTaskExtension>("jacoco") {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
}

tasks.register<JacocoReport>("jacocoDebugUnitTestReport") {
    group = "verification"
    description = "Generates an aggregate JaCoCo report for debug pure unit tests."

    val coveredProjects = subprojects.filter { project -> project.path in jacocoCoveredProjects }

    dependsOn(coveredProjects.mapNotNull { project -> project.tasks.findByName("testDebugUnitTest") })

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        coveredProjects.map { project ->
            project.files(
                project.fileTree("${project.layout.buildDirectory.get()}/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes") {
                    exclude(jacocoClassExcludes)
                },
                project.fileTree("${project.layout.buildDirectory.get()}/intermediates/javac/debug/compileDebugJavaWithJavac/classes") {
                    exclude(jacocoClassExcludes)
                }
            )
        }
    )
    sourceDirectories.setFrom(coveredProjects.map { project -> project.file("src/main/java") })
    executionData.setFrom(
        coveredProjects.map { project ->
            project.fileTree(project.layout.buildDirectory) {
                include(
                    "jacoco/testDebugUnitTest.exec",
                    "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
                )
            }
        }
    )
}

tasks.register<JacocoCoverageVerification>("jacocoDebugUnitTestCoverageVerification") {
    group = "verification"
    description = "Verifies aggregate JaCoCo coverage for debug pure unit tests."

    dependsOn(tasks.named("jacocoDebugUnitTestReport"))

    val coveredProjects = subprojects.filter { project -> project.path in jacocoCoveredProjects }

    classDirectories.setFrom(
        coveredProjects.map { project ->
            project.files(
                project.fileTree("${project.layout.buildDirectory.get()}/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes") {
                    exclude(jacocoClassExcludes)
                },
                project.fileTree("${project.layout.buildDirectory.get()}/intermediates/javac/debug/compileDebugJavaWithJavac/classes") {
                    exclude(jacocoClassExcludes)
                }
            )
        }
    )
    sourceDirectories.setFrom(coveredProjects.map { project -> project.file("src/main/java") })
    executionData.setFrom(
        coveredProjects.map { project ->
            project.fileTree(project.layout.buildDirectory) {
                include(
                    "jacoco/testDebugUnitTest.exec",
                    "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
                )
            }
        }
    )

    violationRules {
        rule {
            enabled = true
            element = "BUNDLE"

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.50".toBigDecimal()
            }
        }
    }
}
