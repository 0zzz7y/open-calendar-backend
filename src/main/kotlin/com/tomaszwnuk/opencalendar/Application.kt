package com.tomaszwnuk.opencalendar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EntityScan(basePackages = ["com.tomaszwnuk.dailyassistant"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
