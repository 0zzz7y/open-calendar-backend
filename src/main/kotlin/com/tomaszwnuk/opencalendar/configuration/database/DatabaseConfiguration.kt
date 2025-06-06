/*
 * Copyright (c) Tomasz Wnuk
 */

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
        "com.tomaszwnuk.opencalendar.domain.calendar",
        "com.tomaszwnuk.opencalendar.domain.category",
        "com.tomaszwnuk.opencalendar.domain.task",
        "com.tomaszwnuk.opencalendar.domain.note",
        "com.tomaszwnuk.opencalendar.domain.event"
    ]
)
class DatabaseConfiguration(
    @Value("\${spring.datasource.url}") private val _url: String,
    @Value("\${spring.datasource.driver-class-name}") private val _driverClassName: String,
    @Value("\${spring.datasource.username:}") private val _username: String?,
    @Value("\${spring.datasource.password:}") private val _password: String?
) {

    @Bean
    @Primary
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.url = _url
        dataSource.setDriverClassName(_driverClassName)

        if (!_username.isNullOrBlank()) dataSource.username = _username
        if (!_password.isNullOrBlank()) dataSource.password = _password

        return dataSource
    }

}
