import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

allOpen{
    annotation("com.zipper.framework.core.annotation.NoArgs")
}

dependencies {
    implementation(Libs.Alibaba.ThreadLocal)

    implementation(Libs.Ip2region.Core)


}
