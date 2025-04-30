package com.tomaszwnuk.opencalendar.domain.calendar

import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.assertNameDoesNotExist
import com.tomaszwnuk.opencalendar.utility.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

@Service
class CalendarService(
    private val _calendarRepository: CalendarRepository
) {

    private var _timer: Long = 0

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allCalendars"], allEntries = true)
        ]
    )
    fun create(dto: CalendarDto): CalendarDto {
        info(this, "Creating $dto")
        _timer = System.currentTimeMillis()
        _calendarRepository.assertNameDoesNotExist(
            name = dto.title,
            existsByName = { _calendarRepository.existsByTitle(it) }
        )

        val calendar = Calendar(
            title = dto.title,
            emoji = dto.emoji
        )
        val created: Calendar = _calendarRepository.save(calendar)

        info(this, "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    @Cacheable(cacheNames = ["allCalendars"], condition = "#result != null")
    fun getAll(): List<CalendarDto> {
        info(this, "Fetching all calendars")
        _timer = System.currentTimeMillis()

        val calendars: List<Calendar> = _calendarRepository.findAll()

        info(this, "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["calendarById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): CalendarDto {
        info(this, "Fetching calendar with id $id")
        _timer = System.currentTimeMillis()

        val calendar: Calendar = _calendarRepository.findOrThrow(id)

        info(this, "Found $calendar in ${System.currentTimeMillis() - _timer} ms")
        return calendar.toDto()
    }

    fun filter(filter: CalendarFilterDto): List<CalendarDto> {
        info(this, "Filtering calendars with $filter")
        _timer = System.currentTimeMillis()

        val calendars: List<Calendar> = _calendarRepository.filter(
            title = filter.title,
            emoji = filter.emoji
        )

        info(this, "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars.map { it.toDto() }
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["calendarById"], key = "#id"),
            CacheEvict(cacheNames = ["allCalendars"], allEntries = true)
        ]
    )
    fun update(id: UUID, dto: CalendarDto): CalendarDto {
        info(this, "Updating $dto")
        _timer = System.currentTimeMillis()

        val existing: Calendar = _calendarRepository.findOrThrow(id = id)
        val isNameChanged: Boolean = !(dto.title.equals(existing.title, ignoreCase = true))
        if (isNameChanged) {
            _calendarRepository.assertNameDoesNotExist(
                name = dto.title,
                existsByName = { _calendarRepository.existsByTitle(it) }
            )
        }

        val changed: Calendar = existing.copy(
            title = dto.title,
            emoji = dto.emoji
        )
        val updated: Calendar = _calendarRepository.save(changed)

        info(this, "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["calendarById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allCalendars"], allEntries = true)
        ]
    )
    fun delete(id: UUID) {
        info(this, "Deleting calendar with id $id.")
        _timer = System.currentTimeMillis()

        val existing: Calendar = _calendarRepository.findOrThrow(id = id)
        _calendarRepository.delete(existing)

        info(this, "Deleted calendar $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
