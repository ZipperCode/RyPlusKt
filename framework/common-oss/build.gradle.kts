import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-redis"))
    implementation(project(":framework:boot-starter-json"))
//    implementation(Libs.SaToken.SpringBootStarter)
//    implementation(Libs.SaToken.Core)
//    implementation(Libs.SaToken.Jwt)
    implementation(Libs.Amazon.AwsJavaSdkS3)
    implementation(Libs.Apache.Commons.Lang3)
}
