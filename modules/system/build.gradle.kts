import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {

    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-web"))
    implementation(project(":framework:boot-starter-mybatis"))
    implementation(project(":framework:boot-starter-security"))
    implementation(project(":framework:boot-starter-redis"))
    implementation(project(":framework:boot-starter-json"))
    implementation(project(":framework:boot-starter-tenant"))
    implementation(project(":framework:boot-starter-log"))
    implementation(project(":framework:boot-starter-translation"))
    implementation(project(":framework:boot-starter-satoken"))
    implementation(project(":framework:boot-starter-idempotent"))
    implementation(project(":framework:boot-starter-websocket"))
    implementation(project(":framework:boot-starter-encrypt"))
    implementation(project(":framework:common-oss"))
    implementation(project(":framework:common-sensitive"))
    implementation(project(":framework:common-excel"))

    implementation(Libs.BaoMiDou.MybatisPlus.PlusBootStarter)


    implementation(Libs.Apache.Commons.Lang3)

    implementation(Libs.Alibaba.EasyExcel)

    implementation(Libs.BaoMiDou.DynamicDataSource.Core)
    implementation(Libs.SaToken.SpringBootStarter)

    implementation(Libs.Redisson.Core)

    kapt(Libs.BaoMiDou.MybatisPlus.Generator)
}
