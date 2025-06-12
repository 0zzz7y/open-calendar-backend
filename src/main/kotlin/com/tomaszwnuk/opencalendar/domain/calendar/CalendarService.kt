package com.tomaszwnuk.opencalendar.domain.calendar

import com.tomaszwnuk.opencalendar.domain.user.UserService
import com.tomaszwnuk.opencalendar.utility.logger.info
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

@Service
class CalendarService(
    private val _calendarRepository: CalendarRepository,
    private val _userService: UserService
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

        val userId: UUID = _userService.getCurrentUserId()
        val existsByName: Boolean = _calendarRepository.existsByNameAndUserId(
            name = dto.name,
            userId = _userService.getCurrentUserId()
        )
        if (existsByName) throw IllegalArgumentException("Calendar with name '${dto.name}' already exists for user $userId")

        val calendar = Calendar(
            name = dto.name,
            emoji = dto.emoji,
            userId = userId
        )
        val created: Calendar = _calendarRepository.save(calendar)

        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    @Cacheable(cacheNames = ["allCalendars"], condition = "#result != null")
    fun getAll(): List<CalendarDto> {
        info(source = this, message = "Fetching all calendars")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val calendars: List<Calendar> = _calendarRepository.findAllByUserId(userId = userId)

        info(source = this, message = "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["calendarById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): CalendarDto {
        info(source = this, message = "Fetching calendar with id $id")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val calendar: Calendar = _calendarRepository.findByIdAndUserId(id = id, userId = userId).orElseThrow {
            NoSuchElementException("Calendar with id $id not found for user $userId")
        }

        info(source = this, message = "Found $calendar in ${System.currentTimeMillis() - _timer} ms")
        return calendar.toDto()
    }

    fun filter(filter: CalendarFilterDto): List<CalendarDto> {
        info(source = this, message = "Filtering calendars with $filter")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val calendars: List<Calendar> = _calendarRepository.filter(
            name = filter.name,
            emoji = filter.emoji,
            userId = userId
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

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Calendar = _calendarRepository.findByIdAndUserId(id = id, userId = userId)
            .orElseThrow { NoSuchElementException("Calendar with id $id not found for user $userId") }
        val isNameChanged: Boolean = !(dto.name.equals(other = existing.name, ignoreCase = true))
        if (isNameChanged) {
            val existsByName: Boolean = _calendarRepository.existsByNameAndUserId(
                name = dto.name,
                userId = userId
            )
            if (existsByName) {
                throw IllegalArgumentException("Calendar with name '${dto.name}' already exists for user $userId")
            }
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

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Calendar = _calendarRepository.findByIdAndUserId(id = id, userId = userId)
            .orElseThrow { NoSuchElementException("Calendar with id $id not found for user $userId") }
        _calendarRepository.delete(existing)

        info(source = this, message = "Deleted calendar $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
