import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(Libs.Jakarta.Mail)
    implementation(Libs.Eclipse.Mail)
}
