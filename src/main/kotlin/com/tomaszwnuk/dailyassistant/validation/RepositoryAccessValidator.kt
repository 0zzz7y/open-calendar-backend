package com.tomaszwnuk.dailyassistant.validation

import com.tomaszwnuk.dailyassistant.domain.utility.logger
import org.springframework.data.jpa.repository.JpaRepository

inline fun <reified T, ID : Any> JpaRepository<T, ID>.findOrThrow(
    id: ID,
    field: String = "id"
): T {
    return this.findById(id).orElseThrow {
        logger().error("${T::class.simpleName} with $field=$id not found.")
        NoSuchElementException("${T::class.simpleName} not found.")
    }
}

inline fun <reified T> JpaRepository<T, *>.assertNameDoesNotExist(
    name: String,
    existsByName: (String) -> Boolean
) {
    if (existsByName(name)) {
        logger().error("${T::class.simpleName} with name='$name' already exists.")
        throw IllegalArgumentException("${T::class.simpleName} with this name already exists.")
    }
}
