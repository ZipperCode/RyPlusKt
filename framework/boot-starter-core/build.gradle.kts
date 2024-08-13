import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(Libs.SpringBoot.Starter)
    implementation(Libs.SpringBoot.Web)
    implementation(Libs.SpringBoot.Validation)
    implementation(Libs.SpringBoot.Aop)

    implementation(Libs.Apache.Commons.Lang3)

    implementation(Libs.Hutool.All)
    implementation(Libs.Hutool.Extra)

    implementation(Libs.Alibaba.ThreadLocal)

    implementation(Libs.Ip2region.Core)

    implementation(Libs.MapStruct.PlusStarter)
    annotationProcessor(Libs.MapStruct.PlusProcessor)

}
