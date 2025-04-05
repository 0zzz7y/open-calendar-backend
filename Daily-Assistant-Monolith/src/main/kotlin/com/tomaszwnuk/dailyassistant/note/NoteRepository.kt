package com.tomaszwnuk.dailyassistant.note

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface NoteRepository : JpaRepository<Note, UUID> {

    fun findAllByCategoryId(categoryId: UUID): List<Note>

}
