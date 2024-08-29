package org.zipper.build.plugin

import org.gradle.kotlin.dsl.DependencyHandlerScope


/**
 * springboot 自动配置
 * 作用：生成配置描述
 */
fun DependencyHandlerScope.applySpringConfuration() {
    // springboot configuration
    compileOnly(Libs.SpringBoot.ConfiguationProcessor)
    kapt(Libs.SpringBoot.ConfiguationProcessor)
}

/**
 * springValidation
 */
fun DependencyHandlerScope.applySpringValidation() {
    implementation(Libs.SpringBoot.Validation)
    compileOnly(Libs.Jakarta.ValidationApi)
    compileOnly(Libs.Hibernate.Validator)
}

/**
 * aop
 */
fun DependencyHandlerScope.applySpringAop() {
    // aop
    implementation(Libs.SpringBoot.Aop)
    // aspectJ
    implementation(Libs.AspectJ.Core)
}

/**
 * springWeb
 */
fun DependencyHandlerScope.applySpringWeb() {
    implementation(Libs.SpringBoot.Web) {
        // 排除tomcat
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")
    }
    implementation(Libs.SpringBoot.Underow)
}

fun DependencyHandlerScope.applySpringDoc() {

}

fun DependencyHandlerScope.applySpringSecurity() {
    implementation(Libs.SpringBoot.Security)
}

fun DependencyHandlerScope.applySpringCache() {
    implementation(Libs.SpringBoot.Cache)
}

/**
 * springSecurity
 */
fun DependencyHandlerScope.applySpringQuartz() {
    implementation(Libs.SpringBoot.Quartz)
}

fun DependencyHandlerScope.applySpringActuator() {
    implementation(Libs.SpringBoot.Actuator)
}

/**
 * springJson
 */
fun DependencyHandlerScope.applySpringJson() {
    implementation(Libs.SpringBoot.Json)
}

/**
 * springWebSocket
 */
fun DependencyHandlerScope.applySpringWebSocket() {
    implementation(Libs.SpringBoot.WebSocket)
}