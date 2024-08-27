import org.apache.tools.ant.filters.ReplaceTokens
import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(project(":framework:boot-starter-core"))
    implementation(project(":framework:boot-starter-web"))
    implementation(project(":framework:boot-starter-mybatis"))
//    implementation(project(":framework:boot-starter-security"))
    implementation(project(":framework:boot-starter-redis"))
    implementation(project(":framework:boot-starter-json"))
    implementation(project(":framework:boot-starter-tenant"))
    implementation(project(":framework:boot-starter-log"))
//    implementation(project(":framework:boot-starter-translation"))
    implementation(project(":framework:boot-starter-satoken"))
//    implementation(project(":framework:boot-starter-idempotent"))
    implementation(project(":framework:boot-starter-websocket"))
    implementation(project(":framework:boot-starter-encrypt"))
    implementation(project(":framework:boot-starter-ratelimiter"))
    implementation(project(":framework:boot-starter-email"))
    implementation(project(":framework:boot-starter-doc"))
//    implementation(project(":framework:common-oss"))
//    implementation(project(":framework:common-sensitive"))
//    implementation(project(":framework:common-excel"))
    implementation(project(":framework:boot-starter-social"))

    implementation(project(":modules:system"))
    implementation(project(":modules:system-storage"))
    implementation(project(":modules:system-tenant"))

    implementation(Libs.Dromara.Sms4j.Core)

    implementation(Libs.SpringBoot.Web) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
}