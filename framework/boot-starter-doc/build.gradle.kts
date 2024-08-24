import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))

//    implementation(Libs.SpringDoc.webApi)
    api(Libs.SpringBoot.Doc)
    implementation(Libs.Therapi.Core)
}
