import org.zipper.build.plugin.Libs
import org.zipper.build.plugin.applyMybatis

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))

//    implementation(Libs.SpringBoot.Web)
//    implementation(Libs.Mybatis.BootStarter)
    implementation(Libs.BaoMiDou.MybatisPlus.PlusBootStarter)
}
