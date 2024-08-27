package org.zipper.build.plugin

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.accessors.runtime.addConfiguredDependencyTo
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class LibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "io.spring.dependency-management")

            dependencies {
                lombok()
                hutool()
                jakarta()
                mapStruct()
                springAutoConfiguration()

//                implementation(Libs.SpringBoot.Starter) {
//                    exclude("org.springframework.boot", "spring-boot-starter-tomcat")
//                }
                implementation(Libs.SpringBoot.Web) {
                     exclude("org.springframework.boot", "spring-boot-starter-tomcat")
                }
                implementation(Libs.SpringBoot.Underow)
                implementation(Libs.SpringBoot.Validation)
                implementation(Libs.AspectJ.Core)
                implementation(Libs.Apache.Commons.Lang3)

            }

            try {
                val kapt = project.extensions.getByName("kapt")
                val method = kapt.javaClass.getDeclaredMethod("setKeepJavacAnnotationProcessors", Boolean::class.java)
                method.invoke(kapt, true)
//                val method2 = kapt.javaClass.getDeclaredMethod("getKeepJavacAnnotationProcessors")
//                println("kapt = $kapt method = ${method2.invoke(kapt)}")
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun DependencyHandlerScope.jakarta() {
        // jakarta库
        implementation(Libs.Jakarta.ValidationApi)
        implementation(Libs.Jakarta.Servlet)
    }

    /**
     * lombok
     */
    private fun DependencyHandlerScope.lombok() {
        // lombok 通用配置
        compileOnly(Libs.Lombok.Core)
        kapt(Libs.Lombok.Core)
    }

    /**
     * springboot 自动配置
     */
    private fun DependencyHandlerScope.springAutoConfiguration() {
        // springboot configuration
        compileOnly(Libs.SpringBoot.ConfiguationProcessor)
        kapt(Libs.SpringBoot.ConfiguationProcessor)
    }

    /**
     * hutoolg工具
     */
    private fun DependencyHandlerScope.hutool() {
        implementation(Libs.Hutool.All)
        implementation(Libs.Hutool.Extra)
    }

    private fun DependencyHandlerScope.mapStruct() {
        implementation(Libs.MapStruct.PlusStarter)
        kapt(Libs.MapStruct.PlusProcessor)
    }


}