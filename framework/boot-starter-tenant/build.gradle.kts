import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-redis"))
    implementation(project(":framework:boot-starter-mybatis"))
    implementation(project(":framework:boot-starter-satoken"))
    implementation(Libs.Apache.Commons.Lang3)
    implementation(Libs.Redisson.Core)
    implementation(Libs.Mybatis.PlusBootStarter)
    implementation(Libs.Alibaba.ThreadLocal)
    implementation(Libs.SaToken.SpringBootStarter)
//    implementation(Libs.SaToken.SpringBootStarter)
}
