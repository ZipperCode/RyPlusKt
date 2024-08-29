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
 * kotlin
 */
fun DependencyHandlerScope.applyKotlin() {
    implementation(Libs.Kotlin.stdLib)
    implementation(Libs.Kotlin.reflect)
}

/**
 * 通用的springBoot依赖
 */
fun DependencyHandlerScope.applyCommonSpringBoot() {
    // 核心启动器，包括自动配置支持、日志记录和 YAML
    implementation(Libs.SpringBoot.Starter)
    // 自动配置
    applySpringConfuration()
    // 验证
    applySpringValidation()
    // aop
    applySpringAop()
    // web
    applySpringWeb()

    // springWeb带有
    compileOnly(Libs.Jakarta.Servlet)
}

/**
 * lombok
 */
fun DependencyHandlerScope.applyLombok() {
    // lombok 通用配置
    compileOnly(Libs.Lombok.Core)
    kapt(Libs.Lombok.Core)
}

/**
 * hutoolg工具
 */
fun DependencyHandlerScope.applyHuTool() {
    implementation(Libs.Hutool.All)
    implementation(Libs.Hutool.Extra)
}

/**
 * mapStruct bean隐射
 */
fun DependencyHandlerScope.applyMapStruct() {
    implementation(Libs.MapStruct.PlusStarter)
    kapt(Libs.MapStruct.PlusProcessor)
}

fun DependencyHandlerScope.applyMybatis() {
    implementation(Libs.BaoMiDou.Mybatis.BootStarter)
    implementation(Libs.BaoMiDou.MybatisPlus.PlusBootStarter)
    implementation(Libs.BaoMiDou.MybatisPlus.Annotation)
    implementation(Libs.BaoMiDou.MybatisPlus.Generator)
}

/**
 * 通用库
 */
fun DependencyHandlerScope.applyCommonLibrary() {
    applyKotlin()
    // 基础的spring依赖
    applyCommonSpringBoot()
    // lombok
    applyLombok()
    // hutool
    applyHuTool()
    // mapSturct
    applyMapStruct()
    // 工具类
    implementation(Libs.Apache.Commons.Lang3)
}

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