/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * The main application class for the OpenCalendar project.
 * Configures and initializes the Spring Boot application with additional features:
 * - JPA Auditing for automatic handling of entity auditing fields.
 * - Caching for improved performance.
 * - Entity scanning to detect JPA entities in the specified package.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EntityScan(basePackages = ["com.tomaszwnuk.opencalendar"])
class Application

/**
 * The main entry point of the application.
 * Launches the Spring Boot application with the provided command-line arguments.
 *
 * @param args Command-line arguments passed to the application.
 */
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
