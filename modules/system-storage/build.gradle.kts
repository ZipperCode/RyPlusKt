import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-mybatis"))
    implementation(project(":framework:boot-starter-json"))
    implementation(project(":framework:boot-starter-doc"))

    implementation(project(":framework:boot-starter-web"))

    implementation(project(":framework:boot-starter-satoken"))

    implementation(project(":framework:boot-starter-idempotent"))
    implementation(project(":framework:boot-starter-log"))
    implementation(project(":framework:common-oss"))
}