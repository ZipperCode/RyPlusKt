import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))

//    implementation(Libs.SpringDoc.webApi)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.6.0")
    implementation(Libs.Therapi.Core)
}
