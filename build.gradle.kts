import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

group = "org.zipper"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

allprojects {
    group = "org.zipper.ruoyi"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }

//    kapt {
//        keepJavacAnnotationProcessors = true
//    }
}

subprojects {
    afterEvaluate {
//        if (childProjects.isEmpty()) {
//            apply(plugin = "java")
//            apply(plugin = "org.jetbrains.kotlin.jvm")
//            apply(plugin = "org.springframework.boot")
//            apply(plugin = "org.jetbrains.kotlin.plugin.spring")
//            apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
//        }
    }
}