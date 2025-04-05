package com.tomaszwnuk.dailyassistant.calendar

import com.tomaszwnuk.dailyassistant.domain.info
import com.tomaszwnuk.dailyassistant.domain.validation.findOrThrow
import org.springframework.stereotype.Service
import java.util.*

@Service
class CalendarService(
    private val _calendarRepository: CalendarRepository
) {

    fun getAll(): List<Calendar> = _calendarRepository.findAll()

    fun getById(id: UUID): Calendar {
        info(this, "Fetching calendar with id $id")
        val calendar: Calendar = _calendarRepository.findOrThrow(id)

        info(this, "Found $calendar")
        return calendar
    }

    fun create(dto: CalendarDto): Calendar {
        info(this, "Creating $dto")
        val calendar = Calendar()

        info(this, "Created $calendar")
        return _calendarRepository.save(calendar)
    }

    fun update(id: UUID, dto: CalendarDto): Calendar {
        info(this, "Updating $dto")
        val existing: Calendar = getById(id)
        val updated: Calendar = existing.copy(id = id)

        info(this, "Updated $updated")
        return _calendarRepository.save(updated)
    }

    fun delete(id: UUID) {
        info(this, "Deleting calendar with id $id.")
        val existing: Calendar = getById(id)

        info(this, "Deleting calendar $existing")
        _calendarRepository.delete(existing)
    }

}
