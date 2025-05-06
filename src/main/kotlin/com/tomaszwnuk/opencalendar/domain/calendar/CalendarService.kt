/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.calendar

import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.assertNameDoesNotExist
import com.tomaszwnuk.opencalendar.utility.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service class for managing calendar entities.
 * Provides methods for creating, retrieving, updating, and deleting calendars.
 *
 * @property _calendarRepository The repository for accessing calendar data.
 */
@Service
class CalendarService(
    private val _calendarRepository: CalendarRepository
) {

    /**
     * Timer used for logging execution time of operations.
     */
    private var _timer: Long = 0

    /**
     * Creates a new calendar.
     * Evicts the cache for all calendars after creation.
     *
     * @param dto The data transfer object containing calendar details.
     *
     * @return The created calendar as a DTO.
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allCalendars"], allEntries = true)
        ]
    )
    fun create(dto: CalendarDto): CalendarDto {
        info(source = this, message = "Creating $dto")
        _timer = System.currentTimeMillis()

        _calendarRepository.assertNameDoesNotExist(
            name = dto.title,
            existsByName = { _calendarRepository.existsByTitle(title = it) }
        )

        val calendar = Calendar(
            title = dto.title,
            emoji = dto.emoji
        )
        val created: Calendar = _calendarRepository.save(calendar)

        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    /**
     * Retrieves all calendars.
     * Caches the result if it is not null.
     *
     * @return A list of all calendars as DTOs.
     */
    @Cacheable(cacheNames = ["allCalendars"], condition = "#result != null")
    fun getAll(): List<CalendarDto> {
        info(source = this, message = "Fetching all calendars")
        _timer = System.currentTimeMillis()

        val calendars: List<Calendar> = _calendarRepository.findAll()

        info(source = this, message = "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars.map { it.toDto() }
    }

    /**
     * Retrieves a calendar by its ID.
     * Caches the result if the ID is not null.
     *
     * @param id The unique identifier of the calendar.
     *
     * @return The calendar as a DTO.
     */
    @Cacheable(cacheNames = ["calendarById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): CalendarDto {
        info(source = this, message = "Fetching calendar with id $id")
        _timer = System.currentTimeMillis()

        val calendar: Calendar = _calendarRepository.findOrThrow(id)

        info(source = this, message = "Found $calendar in ${System.currentTimeMillis() - _timer} ms")
        return calendar.toDto()
    }

    /**
     * Filters calendars based on the provided criteria.
     *
     * @param filter The filter criteria for calendars.
     *
     * @return A list of calendars matching the filter as DTOs.
     */
    fun filter(filter: CalendarFilterDto): List<CalendarDto> {
        info(source = this, message = "Filtering calendars with $filter")
        _timer = System.currentTimeMillis()

        val calendars: List<Calendar> = _calendarRepository.filter(
            title = filter.title,
            emoji = filter.emoji
        )

        info(source = this, message = "Found $calendars in ${System.currentTimeMillis() - _timer} ms")
        return calendars.map { it.toDto() }
    }

    /**
     * Updates an existing calendar.
     * Evicts the cache for the specific calendar and all calendars after updating.
     *
     * @param id The unique identifier of the calendar to update.
     * @param dto The data transfer object containing updated calendar details.
     *
     * @return The updated calendar as a DTO.
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

        val existing: Calendar = _calendarRepository.findOrThrow(id = id)
        val isNameChanged: Boolean = !(dto.title.equals(other = existing.title, ignoreCase = true))
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

        info(source = this, message = "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    /**
     * Deletes a calendar by its ID.
     * Evicts the cache for the specific calendar and all calendars after deletion.
     *
     * @param id The unique identifier of the calendar to delete.
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

        val existing: Calendar = _calendarRepository.findOrThrow(id = id)
        _calendarRepository.delete(existing)

        info(source = this, message = "Deleted calendar $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
