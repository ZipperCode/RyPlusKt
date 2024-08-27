import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

allOpen{
    annotation("com.zipper.framework.core.annotation.NoArgs")
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
    kapt(Libs.MapStruct.PlusProcessor)

    api(Libs.Google.Guava)
}
