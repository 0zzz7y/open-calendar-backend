package com.tomaszwnuk.dailyassistant.calendar

import com.tomaszwnuk.dailyassistant.domain.utility.info
import com.tomaszwnuk.dailyassistant.validation.assertNameDoesNotExist
import com.tomaszwnuk.dailyassistant.validation.findOrThrow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class CalendarService(
    private val _calendarRepository: CalendarRepository
) {

    fun create(dto: CalendarDto): Calendar {
        info(this, "Creating $dto")
        _calendarRepository.assertNameDoesNotExist(
            name = dto.name,
            existsByName = { _calendarRepository.existsByName(it) }
        )
        val calendar = Calendar(name = dto.name)

        info(this, "Created $calendar")
        return _calendarRepository.save(calendar)
    }

    fun getAll(): List<Calendar> {
        info(this, "Fetching all calendars")
        val calendars: List<Calendar> = _calendarRepository.findAll()

        info(this, "Found $calendars")
        return calendars
    }

    fun getAll(pageable: Pageable): Page<Calendar> {
        info(this, "Fetching all calendars")
        val calendars: Page<Calendar> = _calendarRepository.findAll(pageable)

        info(this, "Found $calendars")
        return calendars
    }

    fun getById(id: UUID): Calendar {
        info(this, "Fetching calendar with id $id")
        val calendar: Calendar = _calendarRepository.findOrThrow(id)

        info(this, "Found $calendar")
        return calendar
    }

    fun filter(filter: CalendarFilterDto, pageable: Pageable): Page<Calendar> {
        info(this, "Filtering calendars with $filter")
        val calendars: Page<Calendar> = _calendarRepository.filter(
            name = filter.name,
            pageable = pageable
        )

        info(this, "Found $calendars")
        return calendars
    }

    fun update(id: UUID, dto: CalendarDto): Calendar {
        info(this, "Updating $dto")
        val existing: Calendar = getById(id)

        val isNameChanged: Boolean = !(dto.name.equals(existing.name, ignoreCase = true))
        if (isNameChanged) {
            _calendarRepository.assertNameDoesNotExist(
                name = dto.name,
                existsByName = { _calendarRepository.existsByName(it) }
            )
        }

        val updated = existing.copy(name = dto.name)

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
