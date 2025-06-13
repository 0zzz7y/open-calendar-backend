package com.tomaszwnuk.opencalendar.configuration.database

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.sql.DataSource

@Suppress("unused")
@Configuration
@EnableJpaRepositories(basePackages = ["com.tomaszwnuk.opencalendar.domain"])
class DatabaseConfiguration {

    @Bean
    @Primary
    fun dataSource(properties: DataSourceProperties): DataSource {
        return properties.initializeDataSourceBuilder().build()
    }

}
