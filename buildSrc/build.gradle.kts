plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
}

dependencies {
//    compileOnly("org.jetbrains.kotlin.gradle.plugin:1.9.20")
    compileOnly("org.springframework.boot:spring-boot-gradle-plugin:3.3.1")
    compileOnly("org.jetbrains.kotlin:kotlin-allopen:1.9.20")
}

gradlePlugin {
    plugins {
        register("library") {
            id = "library"
            implementationClass = "org.zipper.build.plugin.LibraryPlugin"
        }
        register("module") {
            id = "module"
            implementationClass = "org.zipper.build.plugin.ModulePlugin"
        }
    }
}