package org.zipper.build.plugin

object Libs {

    object Kotlin {
        val stdLib get() = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        val reflect get() = "org.jetbrains.kotlin:kotlin-reflect"
    }

    object Spring {
        const val ContextSupport = "org.springframework:spring-context-support"
    }

    object SpringBoot {
        const val Starter = "org.springframework.boot:spring-boot-starter"
        const val Web = "org.springframework.boot:spring-boot-starter-web"

        const val ConfiguationProcessor = "org.springframework.boot:spring-boot-configuration-processor"
        const val Actuator = "org.springframework.boot:spring-boot-starter-actuator"

        const val Aop = "org.springframework.boot:spring-boot-starter-aop"
        const val Cache = "org.springframework.boot:spring-boot-starter-cache"
        const val Security = "org.springframework.boot:spring-boot-starter-security"
        const val WebSocket = "org.springframework.boot:spring-boot-starter-websocket"
        const val DataRedis = "org.springframework.boot:spring-boot-starter-data-redis"
        const val Quartz = "org.springframework.boot:spring-boot-starter-quartz"
        const val Validation = "org.springframework.boot:spring-boot-starter-validation"

        const val Doc = "org.springdoc:springdoc-openapi-starter-webmvc-api:2.6.0"

        const val Json = "org.springframework.boot:spring-boot-starter-json"

        const val Underow = "org.springframework.boot:spring-boot-starter-undertow"
    }

    object SpringDoc {

        private const val Version = "1.7.0"

        val webApi get() = "org.springdoc:springdoc-openapi-starter-webmvc-api:${Version}"
    }

    object Jakarta {
        const val Servlet = "jakarta.servlet:jakarta.servlet-api:6.0.0"
        const val ValidationApi = "jakarta.validation:jakarta.validation-api:3.0.2"
        const val Mail = "jakarta.mail:jakarta.mail-api:2.1.2"
    }

    object AspectJ {
        const val Core = "org.aspectj:aspectjweaver:1.9.7"
    }

    object Hibernate {
        const val Validator = "org.hibernate:hibernate-validator:8.0.1.Final"
    }

    object Mybatis {
        const val BootStarter = "org.mybatis.spring.boot:mybatis-spring-boot-starter:${Versions.Mybatis}"
        const val PlusBootStarter = "com.baomidou:mybatis-plus-spring-boot3-starter:${Versions.MybatisPlus}"
        const val Generator = "com.baomidou:mybatis-plus-generator:${Versions.MybatisPlus}"
        const val Annotation = "com.baomidou:mybatis-plus-annotation:${Versions.MybatisPlus}"
    }

    object Mysql{
        const val Connector = "com.mysql:mysql-connector-j:8.2.0"
    }

    object BaoMiDou {
        object Mybatis {
            const val BootStarter = "org.mybatis.spring.boot:mybatis-spring-boot-starter:${Versions.Mybatis}"
        }

        object MybatisPlus {
            const val PlusBootStarter = "com.baomidou:mybatis-plus-spring-boot3-starter:${Versions.MybatisPlus}"
            const val Generator = "com.baomidou:mybatis-plus-generator:${Versions.MybatisPlus}"
            const val Annotation = "com.baomidou:mybatis-plus-annotation:${Versions.MybatisPlus}"
        }

        // dynamic-datasource 多数据源
        object DynamicDataSource {
            const val Core = "com.baomidou:dynamic-datasource-spring-boot3-starter:${Versions.DynamicDataSource}"
        }
    }



    object Hutool {
        const val All = "cn.hutool:hutool-all:${Versions.Hutool}"
        const val Extra = "cn.hutool:hutool-extra:${Versions.Hutool}"
    }

    object Lombok {
        private const val VERSION = "1.18.30"
        const val Core = "org.projectlombok:lombok:${VERSION}"
    }

    object Alibaba {
        private const val EasyExcelVersion = "4.0.1"
        private const val ThreadLocalVersion = "2.14.4"

        private const val FastJsonVersion = "1.2.83"

        // excel导出工具
        const val EasyExcel = "com.alibaba:easyexcel:${EasyExcelVersion}"

        const val ThreadLocal = "com.alibaba:transmittable-thread-local:${ThreadLocalVersion}"

        const val FastJson = "com.alibaba:fastjson:${FastJsonVersion}"
    }

    object EasyExcel {
        const val Core = "com.alibaba:easyexcel:${Versions.EasyExcel}"
    }

    object Apache {
        const val PoiCore = "org.apache.poi:poi:${Versions.ApachePoi}"
        const val PoiOOXml = "org.apache.poi:poi-ooxml:${Versions.ApachePoi}"

        // velocity代码生成使用模板
        const val Velocity = "org.apache.velocity:velocity-engine-core:${Versions.ApacheVelocity}"

        object Commons {
            const val Lang3 = "org.apache.commons:commons-lang3:3.12.0"
            const val Pool = "org.apache.commons:commons-pool:2.11.1"
            const val Pool2 = "org.apache.commons:commons-pool2:2.11.1"
        }

    }

    // Sa-Token 权限认证
    object SaToken {
        val SpringBootStarter = "cn.dev33:sa-token-spring-boot3-starter:${Versions.SaToken}"
        const val Core = "cn.dev33:sa-token-core:${Versions.SaToken}"
        const val Jwt = "cn.dev33:sa-token-jwt:${Versions.SaToken}"
    }

    // sql性能分析插件
    object P6spy {
//        const val Core = "com.p6spy:p6spy:3.9.1"
        const val Core = "p6spy:p6spy:3.9.1"
    }

    object Squareup {
        const val Okhttp3 = "com.squareup.okhttp3:okhttp:4.10.0"
    }

    object Amazon {
        // 亚马逊对象云存储
        const val AwsJavaSdkS3 = "com.amazonaws:aws-java-sdk-s3:1.12.600"
    }

    object SpringBootAdmin {
        private const val Version = "3.1.8"
        const val Core = "de.codecentric:spring-boot-admin-starter-server:${Version}"
        const val Client = "de.codecentric:spring-boot-admin-starter-client:${Version}"
    }

    object Redisson {
        const val Core = "org.redisson:redisson-spring-boot-starter:3.34.1"
    }

    object Log4j {
        private const val Version = "2.2.5"
        const val RedissonLog4j = "com.baomidou:lock4j-redisson-spring-boot-starter:${Version}"
    }

    object PowerJob {
        private const val Version = "4.3.6"
        const val Worker = "tech.powerjob:powerjob-worker-spring-boot-starter:${Version}"
        const val OfficialProcessor = "tech.powerjob:powerjob-official-processors:${Version}"
    }

    object BouncyCastle {
        private const val Version = "1.76"
        const val Core = "org.bouncycastle:bcprov-jdk15to18:${Version}"
    }

    object MapStruct {
        private const val PlusVersion = "1.3.5"
        const val PlusStarter = "io.github.linpeilie:mapstruct-plus-spring-boot-starter:${PlusVersion}"
        const val PlusProcessor = "io.github.linpeilie:mapstruct-plus-processor:${PlusVersion}"
    }

    // 离线IP地址定位库 ip2region
    object Ip2region {
        private const val Version = "2.7.0"
        const val Core = "org.lionsoul:ip2region:${Version}"
    }

    object Therapi {
        private const val Version = "0.15.0"
        const val Core = "com.github.therapi:therapi-runtime-javadoc:${Version}"
    }

    object Jackson {
        private const val Version = "2.17.2"
        const val ModuleKotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:${Version}"
    }

    object Eclipse {
        const val Mail = "org.eclipse.angus:jakarta.mail:1.1.0"
    }

    object JustAuth {
        private const val Version = "1.16.6"
        const val Core = "me.zhyd.oauth:JustAuth:${Version}"
    }

    object Dromara {
        object Sms4j {
            const val Core = "org.dromara.sms4j:sms4j:2.2.0"
        }
    }

    object Google {
        const val Guava = "com.google.guava:guava:31.1-jre"
    }
}