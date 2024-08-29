import org.zipper.build.plugin.Libs
import org.zipper.build.plugin.implementation

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-redis"))
    implementation(project(":framework:boot-starter-mybatis"))
    implementation(project(":framework:boot-starter-satoken"))
    implementation(Libs.Redisson.Core)
    implementation(Libs.BaoMiDou.MybatisPlus.PlusBootStarter)
    implementation(Libs.Alibaba.ThreadLocal)
    implementation(Libs.SaToken.SpringBootStarter)
}
