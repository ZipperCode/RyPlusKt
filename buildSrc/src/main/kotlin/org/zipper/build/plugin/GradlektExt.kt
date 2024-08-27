package org.zipper.build.plugin

import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import org.gradle.kotlin.dsl.project

/**
 * 通用扩展
 */
fun DependencyHandlerScope.compileOnly(libs: String) = add("compileOnly", libs)

fun DependencyHandlerScope.annotationProcessor(libs: String) = add("annotationProcessor", libs)

fun DependencyHandlerScope.kapt(libs: String) = add("kapt", libs)

fun DependencyHandlerScope.implementation(libs: String) = add("implementation", libs)
fun DependencyHandlerScope.implementation(project: ProjectDependency) = add("implementation", project)

fun DependencyHandler.`implementation`(
    dependencyNotation: String,
    dependencyConfiguration: Action<ExternalModuleDependency>
): ExternalModuleDependency = addDependencyTo(
    this, "implementation", dependencyNotation, dependencyConfiguration
)

fun ExternalModuleDependency.exclude(group: String, module: String) {

    exclude(mapOf("group" to group, "module" to module))
}

fun org.gradle.api.Project.`allOpen`(configure: Action<org.jetbrains.kotlin.allopen.gradle.AllOpenExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("allOpen", configure)

/**
 * 业务扩展
 */

fun DependencyHandlerScope.applyCommonModule() {
    // 核心模块
    implementation(project(":framework:boot-starter-core"))
    // mybatis
    implementation(project(":framework:boot-starter-mybatis"))
    // json
    implementation(project(":framework:boot-starter-json"))
    // controller 生成文档
    implementation(project(":framework:boot-starter-doc"))
    // web
    implementation(project(":framework:boot-starter-web"))
    // satoekn
    implementation(project(":framework:boot-starter-satoken"))
    // 幂等
    implementation(project(":framework:boot-starter-idempotent"))
    // controller 日志
    implementation(project(":framework:boot-starter-log"))
}