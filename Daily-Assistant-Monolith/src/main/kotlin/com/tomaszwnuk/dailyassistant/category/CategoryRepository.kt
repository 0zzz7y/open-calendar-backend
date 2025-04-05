package com.tomaszwnuk.dailyassistant.category

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CategoryRepository : JpaRepository<Category, UUID> {

    fun existsByName(name: String): Boolean

}
