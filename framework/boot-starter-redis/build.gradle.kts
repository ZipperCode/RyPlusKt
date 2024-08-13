import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))

    implementation(Libs.Apache.Commons.Lang3)
    implementation(Libs.SpringBoot.Web)
//    implementation(Libs.Mybatis.BootStarter)
//    implementation(Libs.SaToken.Core)
    implementation(Libs.Redisson.Core)
    implementation(Libs.Log4j.RedissonLog4j)
    implementation(Libs.Jackson.ModuleKotlin)
}
