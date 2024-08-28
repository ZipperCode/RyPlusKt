import org.zipper.build.plugin.Libs
import org.zipper.build.plugin.applyCommonModule

plugins {
    id("module")
}

dependencies {
    applyCommonModule()
    implementation(project(":framework:boot-starter-tenant"))
    implementation(project(":framework:common-excel"))
}