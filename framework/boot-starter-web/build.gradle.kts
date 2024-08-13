import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-json"))
//    implementation(Libs.SpringBoot.Underow)
    implementation(Libs.SpringBoot.Actuator)
    implementation(Libs.Apache.Commons.Lang3)
    implementation(Libs.Alibaba.ThreadLocal)
//    implementation(Libs.Jakarta.Servlet)
}
