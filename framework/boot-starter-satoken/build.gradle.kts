import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-redis"))
    api(Libs.SaToken.SpringBootStarter)
    implementation(Libs.SaToken.Core)
    implementation(Libs.SaToken.Jwt)
}
