import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-redis"))
    implementation(project(":framework:boot-starter-json"))
    implementation(Libs.Amazon.AwsJavaSdkS3)
}
