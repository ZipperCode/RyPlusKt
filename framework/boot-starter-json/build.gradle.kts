import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(Libs.SpringBoot.Json)
}
