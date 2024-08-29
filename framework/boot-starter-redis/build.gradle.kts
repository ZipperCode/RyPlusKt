import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(Libs.Redisson.Core)
    implementation(Libs.Log4j.RedissonLog4j)
    implementation(Libs.Jackson.ModuleKotlin)
}
