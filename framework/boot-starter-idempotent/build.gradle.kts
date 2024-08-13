import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-redis"))

    implementation(Libs.Apache.Commons.Lang3)
    implementation(Libs.SpringBoot.Web)
//    implementation(Libs.Mybatis.BootStarter)
    implementation(Libs.SaToken.Core)
    implementation(Libs.SpringBoot.DataRedis)
}
