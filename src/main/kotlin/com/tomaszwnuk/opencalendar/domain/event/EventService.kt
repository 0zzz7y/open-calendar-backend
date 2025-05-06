/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.event

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service class for managing events.
 * Provides methods for creating, retrieving, updating, deleting, and filtering events.
 *
 * @property _eventRepository Repository for accessing event data.
 * @property _calendarRepository Repository for accessing calendar data.
 * @property _categoryRepository Repository for accessing category data.
 */
@Service
class EventService(
    private val _eventRepository: EventRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository
) {

    /**
     * Timer used for measuring execution time of operations.
     */
    private var _timer: Long = 0

    /**
     * Creates a new event and saves it to the repository.
     * Evicts relevant caches after creation.
     *
     * @param dto The data transfer object containing event details.
     *
     * @return The created event as a DTO.
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun create(dto: EventDto): EventDto {
        info(source = this, message = "Creating $dto")
        _timer = System.currentTimeMillis()

        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val event = Event(
            title = dto.title,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            recurringPattern = dto.recurringPattern,
            calendar = calendar,
            category = category
        )

        val created: Event = _eventRepository.save(event)

        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    /**
     * Retrieves an event by its unique identifier.
     * Caches the result for future requests.
     *
     * @param id The UUID of the event to retrieve.
     *
     * @return The event as a DTO.
     */
    @Cacheable(cacheNames = ["eventById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): EventDto {
        info(source = this, message = "Fetching event with id $id")
        _timer = System.currentTimeMillis()

        val event: Event = _eventRepository.findOrThrow(id = id)

        info(source = this, message = "Found $event in ${System.currentTimeMillis() - _timer} ms")
        return event.toDto()
    }

    /**
     * Retrieves all events from the repository.
     * Caches the result for future requests.
     *
     * @return A list of all events as DTOs.
     */
    @Cacheable(cacheNames = ["allEvents"], condition = "#result != null")
    fun getAll(): List<EventDto> {
        info(source = this, message = "Fetching all events")
        _timer = System.currentTimeMillis()

        val events: List<Event> = _eventRepository.findAll()

        info(source = this, message = "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events.map { it.toDto() }
    }

    /**
     * Retrieves all events associated with a specific calendar.
     * Caches the result for future requests.
     *
     * @param calendarId The UUID of the calendar.
     *
     * @return A list of events as DTOs.
     */
    @Cacheable(cacheNames = ["calendarEvents"], key = "#calendarId", condition = "#calendarId != null")
    fun getAllByCalendarId(calendarId: UUID): List<EventDto> {
        info(source = this, message = "Fetching all events for calendar with id $calendarId")
        _timer = System.currentTimeMillis()

        val events: List<Event> = _eventRepository.findAllByCalendarId(calendarId = calendarId)

        info(source = this, message = "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events.map { it.toDto() }
    }

    /**
     * Retrieves all events associated with a specific category.
     * Caches the result for future requests.
     *
     * @param categoryId The UUID of the category.
     *
     * @return A list of events as DTOs.
     */
    @Cacheable(cacheNames = ["categoryEvents"], key = "#categoryId", condition = "#categoryId != null")
    fun getAllByCategoryId(categoryId: UUID): List<EventDto> {
        info(source = this, message = "Fetching all events for category with id $categoryId")
        _timer = System.currentTimeMillis()

        val events: List<Event> = _eventRepository.findAllByCategoryId(categoryId = categoryId)

        info(source = this, message = "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events.map { it.toDto() }
    }

    /**
     * Filters events based on the provided criteria.
     *
     * @param filter The filter criteria as a DTO.
     *
     * @return A list of filtered events as DTOs.
     */
    fun filter(filter: EventFilterDto): List<EventDto> {
        info(source = this, message = "Filtering events with $filter")
        _timer = System.currentTimeMillis()

        val filtered: List<Event> = _eventRepository.filter(
            title = filter.title,
            description = filter.description,
            dateFrom = filter.dateFrom,
            dateTo = filter.dateTo,
            recurringPattern = filter.recurringPattern,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId
        )

        info(source = this, message = "Found $filtered in ${System.currentTimeMillis() - _timer} ms")
        return filtered.map { it.toDto() }
    }

    /**
     * Updates an existing event with new details.
     * Evicts relevant caches after the update.
     *
     * @param id The UUID of the event to update.
     * @param dto The data transfer object containing updated event details.
     *
     * @return The updated event as a DTO.
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["eventById"], key = "#id"),
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun update(id: UUID, dto: EventDto): EventDto {
        info(source = this, message = "Updating $dto")
        _timer = System.currentTimeMillis()

        val existing: Event = _eventRepository.findOrThrow(id = id)
        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val updated = existing.copy(
            title = dto.title,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            recurringPattern = dto.recurringPattern,
            calendar = calendar,
            category = category
        )

        val saved: Event = _eventRepository.save(updated)

        info(source = this, message = "Updated $saved in ${System.currentTimeMillis() - _timer} ms")
        return saved.toDto()
    }

    /**
     * Deletes an event by its unique identifier.
     * Evicts relevant caches after deletion.
     *
     * @param id The UUID of the event to delete.
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["eventById"], key = "#id"),
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun delete(id: UUID) {
        info(source = this, message = "Deleting event with id $id.")
        _timer = System.currentTimeMillis()

        val existing: Event = _eventRepository.findOrThrow(id = id)

        _eventRepository.delete(existing)
        info(source = this, message = "Deleted event $existing in \${System.currentTimeMillis() - _timer} ms")
    }

    /**
     * Deletes all events associated with a specific calendar.
     * Evicts relevant caches after deletion.
     *
     * @param calendarId The UUID of the calendar.
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["eventById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun deleteAllByCalendarId(calendarId: UUID) {
        info(source = this, message = "Deleting all events for calendar with id $calendarId.")
        _timer = System.currentTimeMillis()

        val events: List<Event> = _eventRepository.findAllByCalendarId(calendarId = calendarId)
        _eventRepository.deleteAll(events)

        info(
            source = this,
            message = "Deleted all events for calendar with id $calendarId in ${System.currentTimeMillis() - _timer} ms"
        )
    }

    /**
     * Deletes all events associated with a specific category.
     * Evicts relevant caches after deletion.
     *
     * @param categoryId The UUID of the category.
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["eventById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun removeCategoryByCategoryId(categoryId: UUID) {
        info(source = this, message = "Deleting all events for category with id $categoryId.")
        _timer = System.currentTimeMillis()

        val events: List<Event> = _eventRepository.findAllByCategoryId(categoryId = categoryId)
        events.forEach { event ->
            val withoutCategory = event.copy(category = null)
            _eventRepository.save(withoutCategory)
        }

        info(
            source = this,
            message = "Deleted all events for category with id $categoryId in ${System.currentTimeMillis() - _timer} ms"
        )
    }

}
