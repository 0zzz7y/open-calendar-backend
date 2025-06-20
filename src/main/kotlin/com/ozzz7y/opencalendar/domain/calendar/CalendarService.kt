package com.ozzz7y.opencalendar.domain.calendar

import com.ozzz7y.opencalendar.domain.user.UserService
import com.ozzz7y.opencalendar.utility.logger.info
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

/**
 * The service for calendar operations.
 */
@Service
class CalendarService(

    /**
     * The repository for managing calendar data.
     */
    private val _calendarRepository: CalendarRepository,

    /**
     * The service for user operations.
     */
    private val _userService: UserService

) {

    /**
     * The timer for measuring the duration of operations.
     */
    private var _timer: Long = 0

    /**
     * Creates a new calendar.
     *
     * @param dto The data transfer object containing calendar details
     *
     * @return The created calendar as a data transfer object
     *
     * @throws IllegalArgumentException If a calendar with the same name already exists for the user
     */
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
            userId = userId
        )
        if (existsByName) {
            throw IllegalArgumentException("Calendar with name '${dto.name}' already exists for user $userId")
        }

        val calendar = Calendar(
            name = dto.name,
            emoji = dto.emoji,
            userId = userId
        )

        val created: Calendar = _calendarRepository.save(calendar)
        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    /**
     * Retrieves all calendars for the current user.
     *
     * @return A list of all calendars as data transfer objects
     */
    @Cacheable(cacheNames = ["allCalendars"], condition = "#result != null")
    fun getAll(): List<CalendarDto> {
        info(source = this, message = "Fetching all calendars")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val calendars: List<Calendar> = _calendarRepository.findAllByUserId(userId = userId)

        info(source = this, message = "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars.map { it.toDto() }
    }

    /**
     * Retrieves a calendar by its unique identifier.
     *
     * @param id The unique identifier of the calendar
     *
     * @return The calendar as a data transfer object
     *
     * @throws NoSuchElementException If the calendar does not exist for the user
     */
    @Cacheable(cacheNames = ["calendarById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): CalendarDto {
        info(source = this, message = "Fetching calendar with id $id")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val calendar: Optional<Calendar> = _calendarRepository.findByIdAndUserId(id = id, userId = userId)
        if (calendar.isEmpty) {
            throw NoSuchElementException("Calendar with id $id not found for user $userId")
        }

        info(source = this, message = "Found $calendar in ${System.currentTimeMillis() - _timer} ms")
        return calendar.get().toDto()
    }

    /**
     * Filters calendars based on the provided criteria.
     *
     * @param filter The filter criteria
     *
     * @return A list of calendars that match the filter criteria as data transfer objects
     */
    fun filter(filter: CalendarFilterDto): List<CalendarDto> {
        info(source = this, message = "Filtering calendars with $filter")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val calendars: List<Calendar> = _calendarRepository.filter(
            userId = userId,
            name = filter.name,
            emoji = filter.emoji
        )

        info(source = this, message = "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars.map { it.toDto() }
    }

    /**
     * Updates an existing calendar.
     *
     * @param id The unique identifier of the calendar to update
     * @param dto The data transfer object containing the updated details of the calendar
     *
     * @return The updated calendar as a data transfer object
     *
     * @throws NoSuchElementException If the calendar does not exist for the user
     * @throws IllegalArgumentException If a calendar with the same name already exists for the user
     */
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
        val existing: Optional<Calendar> = _calendarRepository.findByIdAndUserId(id = id, userId = userId)
        if (existing.isEmpty) {
            throw NoSuchElementException("Calendar with id $id not found for user $userId")
        }

        val isNameChanged: Boolean = !(dto.name.equals(other = existing.get().name, ignoreCase = true))
        if (isNameChanged) {
            val existsByName: Boolean = _calendarRepository.existsByNameAndUserId(
                name = dto.name,
                userId = userId
            )
            if (existsByName) {
                throw IllegalArgumentException("Calendar with name '${dto.name}' already exists for user $userId")
            }
        }

        val changed: Calendar = existing.get().copy(
            name = dto.name,
            emoji = dto.emoji
        )

        val updated: Calendar = _calendarRepository.save(changed)
        info(source = this, message = "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    /**
     * Deletes a calendar by its unique identifier.
     *
     * @param id The unique identifier of the calendar to delete
     *
     * @throws NoSuchElementException If the calendar does not exist for the user
     */
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
        val existing: Optional<Calendar> = _calendarRepository.findByIdAndUserId(id = id, userId = userId)
        if (existing.isEmpty) {
            throw NoSuchElementException("Calendar with id $id not found for user $userId")
        }

        _calendarRepository.delete(existing.get())
        info(source = this, message = "Deleted calendar $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
