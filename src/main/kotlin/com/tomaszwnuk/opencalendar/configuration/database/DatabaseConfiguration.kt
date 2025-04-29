package com.tomaszwnuk.opencalendar.configuration.database

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Suppress("unused")
@Configuration
@EnableJpaRepositories(
    basePackages = [
        "com.tomaszwnuk.opencalendar.calendar",
        "com.tomaszwnuk.opencalendar.category",
        "com.tomaszwnuk.opencalendar.task",
        "com.tomaszwnuk.opencalendar.note",
        "com.tomaszwnuk.opencalendar.event"
    ]
)
class DatabaseConfiguration(
    @Value("\${spring.datasource.url}") private val url: String,
    @Value("\${spring.datasource.driver-class-name}") private val driverClassName: String,
    @Value("\${spring.datasource.username:}") private val username: String?,
    @Value("\${spring.datasource.password:}") private val password: String?
) {

    @Bean
    @Primary
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.url = url
        dataSource.setDriverClassName(driverClassName)

        if (!username.isNullOrBlank()) dataSource.username = username
        if (!password.isNullOrBlank()) dataSource.password = password

        return dataSource
    }

}