package com.tomaszwnuk.dailyassistant.configuration

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
        "com.tomaszwnuk.dailyassistant.calendar", "com.tomaszwnuk.dailyassistant.category",
        "com.tomaszwnuk.dailyassistant.task", "com.tomaszwnuk.dailyassistant.note", "com.tomaszwnuk.dailyassistant.event"],
    entityManagerFactoryRef = "entityManagerFactory"
) class DatabaseConfiguration {

    @Bean
    @Primary
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()

        dataSource.url = "jdbc:h2:mem:db;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1"
        dataSource.setDriverClassName("org.h2.Driver")
        dataSource.username = "sa"
        dataSource.password = "password"

        return dataSource
    }

    @Bean
    fun secondaryDataSource(@Value("\${spring.datasource.secondary.url:}") secondaryUrl: String): DataSource? {
        if (secondaryUrl.isBlank()) return null

        val dataSource = DriverManagerDataSource()
        dataSource.url = secondaryUrl
        dataSource.setDriverClassName("org.sqlite.JDBC")

        return dataSource
    }

}
