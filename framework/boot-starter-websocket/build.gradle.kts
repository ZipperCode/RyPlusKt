import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-redis"))
    implementation(project(":framework:boot-starter-satoken"))
//    implementation(Libs.SpringBoot.Underow)
    implementation(Libs.Apache.Commons.Lang3)
    implementation(Libs.SpringBoot.WebSocket)
//    implementation(Libs.Alibaba.ThreadLocal)
}
