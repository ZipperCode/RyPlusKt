package org.zipper.build.plugin

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

class ModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.jvm")
            apply(plugin = "org.jetbrains.kotlin.kapt")
            apply(plugin = "org.springframework.boot")
            apply(plugin = "org.jetbrains.kotlin.plugin.spring")
            apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
            apply(plugin = "io.freefair.lombok")
            apply(plugin = "library")

            group = BuildConfig.GROUP
            version = BuildConfig.VERSION

            repositories {
                mavenCentral()
            }

            dependencies {
                // kotlin
//                implementation(Libs.Kotlin.stdLib)
                implementation(Libs.Kotlin.reflect)

            }



            allOpen {
                annotations(
                    listOf(
                        "com.zipper.framework.core.annotation.NoArgs",
                        "lombok.Data"
                    )
                )
            }
        }
    }

}