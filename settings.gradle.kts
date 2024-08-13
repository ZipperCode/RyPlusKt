plugins {
    id("org.springframework.boot") version "3.3.1" apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
    kotlin("jvm") version "1.9.20" apply false
    kotlin("kapt") version "1.9.20" apply false
    kotlin("plugin.spring") version "1.9.20" apply false
    kotlin("plugin.lombok") version "1.9.20" apply false
    id("io.freefair.lombok") version "8.1.0" apply false
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "RuoyiPlusKt"
include(":framework")
include("framework:common-excel")
include("framework:common-oss")
include("framework:common-sensitive")
include("framework:boot-starter-core")
include("framework:boot-starter-doc")
include("framework:boot-starter-encrypt")
include("framework:boot-starter-idempotent")
include("framework:boot-starter-redis")
include("framework:boot-starter-job")
include("framework:boot-starter-json")
include("framework:boot-starter-log")
include("framework:boot-starter-satoken")
include("framework:boot-starter-email")
include("framework:boot-starter-mybatis")
include("framework:boot-starter-ratelimiter")
include("framework:boot-starter-security")
include("framework:boot-starter-social")
include("framework:boot-starter-translation")
include("framework:boot-starter-web")
include("framework:boot-starter-websocket")
include("framework:boot-starter-tenant")

include(":modules")
include("modules:system")
findProject(":modules:system")?.name = "system"
include("server")
