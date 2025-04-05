package com.tomaszwnuk.dailyassistant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = ["com.tomaszwnuk.dailyassistant"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
