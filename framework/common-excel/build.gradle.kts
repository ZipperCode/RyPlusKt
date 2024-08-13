import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(Libs.SpringBoot.Web)
    implementation(Libs.Apache.Commons.Lang3)
    implementation(Libs.Alibaba.EasyExcel)
}
