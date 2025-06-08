package com.tomaszwnuk.opencalendar.domain.calendar

import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.repository.assertNameDoesNotExist
import com.tomaszwnuk.opencalendar.utility.validation.repository.findOrThrow
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
        info(source = this, message = "Creating $dto")
        _timer = System.currentTimeMillis()

        _calendarRepository.assertNameDoesNotExist(
            name = dto.name,
            existsByName = { _calendarRepository.existsByName(name = it) }
        )

        val calendar = Calendar(
            name = dto.name,
            emoji = dto.emoji
        )
        val created: Calendar = _calendarRepository.save(calendar)

        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    @Cacheable(cacheNames = ["allCalendars"], condition = "#result != null")
    fun getAll(): List<CalendarDto> {
        info(source = this, message = "Fetching all calendars")
        _timer = System.currentTimeMillis()

        val calendars: List<Calendar> = _calendarRepository.findAll()

        info(source = this, message = "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["calendarById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): CalendarDto {
        info(source = this, message = "Fetching calendar with id $id")
        _timer = System.currentTimeMillis()

        val calendar: Calendar = _calendarRepository.findOrThrow(id)

        info(source = this, message = "Found $calendar in ${System.currentTimeMillis() - _timer} ms")
        return calendar.toDto()
    }

    fun filter(filter: CalendarFilterDto): List<CalendarDto> {
        info(source = this, message = "Filtering calendars with $filter")
        _timer = System.currentTimeMillis()

        val calendars: List<Calendar> = _calendarRepository.filter(
            name = filter.name,
            emoji = filter.emoji
        )

        info(source = this, message = "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars.map { it.toDto() }
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["calendarById"], key = "#id"),
            CacheEvict(cacheNames = ["allCalendars"], allEntries = true)
        ]
    )
    fun update(id: UUID, dto: CalendarDto): CalendarDto {
        info(source = this, message = "Updating $dto")
        _timer = System.currentTimeMillis()

        val existing: Calendar = _calendarRepository.findOrThrow(id = id)
        val isNameChanged: Boolean = !(dto.name.equals(other = existing.name, ignoreCase = true))
        if (isNameChanged) {
            _calendarRepository.assertNameDoesNotExist(
                name = dto.name,
                existsByName = { _calendarRepository.existsByName(it) }
            )
        }

        val changed: Calendar = existing.copy(
            name = dto.name,
            emoji = dto.emoji
        )
        val updated: Calendar = _calendarRepository.save(changed)

        info(source = this, message = "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["calendarById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allCalendars"], allEntries = true)
        ]
    )
    fun delete(id: UUID) {
        info(source = this, message = "Deleting calendar with id $id.")
        _timer = System.currentTimeMillis()

        val existing: Calendar = _calendarRepository.findOrThrow(id = id)
        _calendarRepository.delete(existing)

        info(source = this, message = "Deleted calendar $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
