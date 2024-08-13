package com.zipper.server

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

/**
 * web容器中进行部署
 *
 * @author Lion Li
 */
class DromaraServletInitializer : SpringBootServletInitializer() {
    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(DromaraApplication::class.java)
    }
}
