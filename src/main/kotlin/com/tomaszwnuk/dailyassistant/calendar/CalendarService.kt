package com.tomaszwnuk.dailyassistant.calendar

import com.tomaszwnuk.dailyassistant.domain.utility.info
import com.tomaszwnuk.dailyassistant.validation.assertNameDoesNotExist
import com.tomaszwnuk.dailyassistant.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class CalendarService(
    private val _calendarRepository: CalendarRepository
) {

    private var _timer: Long = 0

    fun create(dto: CalendarDto): Calendar {
        info(this, "Creating $dto")
        _timer = System.currentTimeMillis()
        _calendarRepository.assertNameDoesNotExist(
            name = dto.name,
            existsByName = { _calendarRepository.existsByName(it) }
        )
        val calendar = Calendar(name = dto.name)

        val created: Calendar = _calendarRepository.save(calendar)
        info(this, "Created $created in ${System.currentTimeMillis() - _timer} ms")

        return created
    }

    @Cacheable(cacheNames = ["allCalendars"])
    fun getAll(pageable: Pageable): Page<Calendar> {
        info(this, "Fetching all calendars")
        _timer = System.currentTimeMillis()
        val calendars: Page<Calendar> = _calendarRepository.findAll(pageable)

        info(this, "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars
    }

    @Cacheable(cacheNames = ["calendarById"], key = "#id")
    fun getById(id: UUID): Calendar {
        info(this, "Fetching calendar with id $id")
        _timer = System.currentTimeMillis()
        val calendar: Calendar = _calendarRepository.findOrThrow(id)

        info(this, "Found $calendar in ${System.currentTimeMillis() - _timer} ms")
        return calendar
    }

    fun filter(filter: CalendarFilterDto, pageable: Pageable): Page<Calendar> {
        info(this, "Filtering calendars with $filter")
        _timer = System.currentTimeMillis()
        val calendars: Page<Calendar> = _calendarRepository.filter(
            name = filter.name,
            pageable = pageable
        )

        info(this, "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["calendarById"], key = "#id"),
        CacheEvict(cacheNames = ["allCalendars"], allEntries = true)
    ])
    fun update(id: UUID, dto: CalendarDto): Calendar {
        info(this, "Updating $dto")
        _timer = System.currentTimeMillis()
        val existing: Calendar = getById(id)

        val isNameChanged: Boolean = !(dto.name.equals(existing.name, ignoreCase = true))
        if (isNameChanged) {
            _calendarRepository.assertNameDoesNotExist(
                name = dto.name,
                existsByName = { _calendarRepository.existsByName(it) }
            )
        }
        val changed = existing.copy(name = dto.name)

        val updated: Calendar = _calendarRepository.save(changed)
        info(this, "Updated $updated in ${System.currentTimeMillis() - _timer} ms")

        return updated
    }

    fun delete(id: UUID) {
        info(this, "Deleting calendar with id $id.")
        _timer = System.currentTimeMillis()
        val existing: Calendar = getById(id)

        _calendarRepository.delete(existing)
        info(this, "Deleting calendar $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
