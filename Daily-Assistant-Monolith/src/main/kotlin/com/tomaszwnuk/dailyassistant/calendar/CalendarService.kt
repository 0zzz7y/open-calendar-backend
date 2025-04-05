package com.tomaszwnuk.dailyassistant.calendar

import org.springframework.stereotype.Service
import java.util.*

@Service
class CalendarService(

    private val _calendarRepository: CalendarRepository

) {

    fun getAll(): List<Calendar> = _calendarRepository.findAll()

    fun getById(id: UUID): Calendar = _calendarRepository.findById(id).orElseThrow {
        NoSuchElementException("Calendar with id $id could not be found.")
    }

    fun create(dto: CalendarDto): Calendar {
        val calendar = Calendar()
        return _calendarRepository.save(calendar)
    }

    fun update(id: UUID, dto: CalendarDto): Calendar {
        val existing = getById(id)
        val updated = existing.copy()
        return _calendarRepository.save(updated)
    }

    fun delete(id: UUID) {
        val existing = getById(id)
        _calendarRepository.delete(existing)
    }

}
