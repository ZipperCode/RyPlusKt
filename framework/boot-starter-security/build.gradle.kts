import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-satoken"))
    implementation(Libs.Apache.Commons.Lang3)
    implementation(Libs.SaToken.SpringBootStarter)
}
