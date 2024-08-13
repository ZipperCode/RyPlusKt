import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-satoken"))

    implementation(Libs.SpringBoot.Web)

    implementation(Libs.BaoMiDou.Mybatis.BootStarter)
    implementation(Libs.BaoMiDou.MybatisPlus.Generator)
    implementation(Libs.BaoMiDou.MybatisPlus.Annotation)
    api(Libs.BaoMiDou.MybatisPlus.PlusBootStarter)
    implementation(Libs.BaoMiDou.DynamicDataSource.Core)

    implementation(Libs.P6spy.Core)

    implementation(Libs.Jackson.ModuleKotlin)

    implementation(Libs.Apache.Commons.Lang3)

    implementation(Libs.Mysql.Connector)
}
