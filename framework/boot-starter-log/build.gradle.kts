import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-satoken"))
    implementation(project(":framework:boot-starter-json"))
    implementation(Libs.Alibaba.ThreadLocal)
    implementation(Libs.SpringBoot.Web)
    implementation(Libs.Apache.Commons.Lang3)
}
